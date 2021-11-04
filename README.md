[TOC]

## 解决环境问题

在macOS环境下可以很轻易地进行socket测试,只要使用nc命令就好了

```shell
nc your_host_inet_addr your_port
# e.g.
nc 127.0.0.1 6996
```

但在windows环境下怎样测试呢?看网上的解决方案,大多是安装一个来路不明的类nc工具.exe,看起来就很可疑的样子.

想到一个办法:

- ubuntu镜像使用host网络模式即可,当然bridge模式也可以

```shell
docker pull ubuntu
# 如果apt在ubuntu中无法使用,先运行下面的命令
apt-get update
apt-get -y install curl
```

- ubuntu镜像使用host网络模式即可

## netty API

### ByteBuf

用来替代Java NIO中的ByteBuffer.看一下ByteBuffer的操作介绍,就知道这个API用起来有多么麻烦了.Netty自创了另一个数据缓冲实现:ByteBuf,大大简化了API操作,同时甚至提升了操作效率.

结构上讲,ByteBuf是由一串字节数组组成的,数组中的每个字节用来存储信息.

不像ByteBuffer只有一个索引,ByteBuf提供了两个索引,一个用来读数据,另一个用来写数据,通过改变两个索引在字节数组中的指向位置,来定位读写信息的位置.

在netty读写数据时,对应的索引也会移动,并根据读或写的字节数递增.

更具体的介绍可以直接看ByteBuf源码的文档,写得很全.

描述ByteBuf的字节有几个概念,ByteBuf的字节类型可以分为4种:

> - discardable bytes
    >
    >   ​	读取完成的字节,表示可以被丢弃并被回收的字节
>
> - readable bytes
    >
    >   ​	可以读取的字节,即在读索引后面的尚未读取的有数据的字节
>
> - writable bytes
    >
    >   ​	可以写的字节,即在写索引后没有填充任何数据的字节
>
> - capacity bytes
    >
    >   ​	ByteBuf分配的最大字节数

### ChannelInboundHandlerAdapter和SimpleChannelInboundHandlr的区别

ChannelInboundHandlerAdapter不会在channelRead方法后释放ByteBuf资源

```java
/**
 * Calls {@link ChannelHandlerContext#fireChannelRead(Object)} to forward
 * to the next {@link ChannelInboundHandler} in the {@link ChannelPipeline}.
 *
 * Sub-classes may override this method to change behavior.
 */
@Skip
@Override
public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ctx.fireChannelRead(msg);
        }
```

继承SimpleChannelInboundHandler需要实现channelRead0方法,超类使用模板方法模式,将channelRead0()方法作为了channelRead()方法的一个步骤,最后去释放资源

```java
@Override
public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        boolean release = true;
        try {
        if (acceptInboundMessage(msg)) {
@SuppressWarnings("unchecked")
      I imsg = (I) msg;
              channelRead0(ctx, imsg);
              } else {
              release = false;
              ctx.fireChannelRead(msg);
              }
              } finally {
              if (autoRelease && release) {
              ReferenceCountUtil.release(msg);
              }
              }
              }
```

因此,**如果服务端channelHandler在channelRead方法中还做了响应客户端的写操作,就不能释放资源(因为相同的ByteBuf会被用来写入数据**)

### netty的ByteBuf缓冲区

netty根据ByteBuf的所在位置不同,分为了三个缓冲区,即ByteBuf的分配区域

> - 堆缓冲区–HeapByteBuf
    >
    >   ​	分配在内存的堆(Heap)上,因为JVM管理,内存分配和回收较快.最大优点是可以**直接使用JVM进行内存管理.**
    >
    >   ​	缺点是因为数据入站需要把数据拷贝到由JVM管理的堆中,数据出站则需要把数据拷贝到内核的channel中,性能有一定损失**(额外拷贝)**.
>
> - 直接缓冲区–DirectByteBuf
    >
    >   ​	数据放在非堆内存中,内存分配和回收相对使用JVM慢一点,但是因为减少了内存拷贝,从channel中读写时比堆内存要快.
>
> - 复合缓冲区–CompositeByteBuf
    >
    >   ​	同时使用上述两种内存

netty默认使用DirectByteBuf缓冲区,如果想切换到HeapByteBuf缓冲区,需要进行如下设置

```
//首先设置系统变量
System.setProperty("io.netty.noUnsafe","true");
//然后,还要设置Bootstrap的属性
serverBootstrap.childOption(ChannelOption.ALLOCATOR,UnpooledByteBufAllocator.DEFAULT);
```

**TODO:我这样设置了之后使用的还是直接内存,是因为netty版本不同,还是因为是操作系统不同?**



### ByteBuf的分配

主要使用的有这两种ByteBuf分配器

> - PooledByteBufAllocator
    >
    >   ​	实现ByteBuf的池化,减少创建ByteBuf和重新分配内存的次数,能提高性能并减少内存碎片
>
> - UnpolledByteBufAllocator
    >
    >   ​	不使用缓冲池,每次创建新的ByteBuf对象

## Netty基本构成

#### Bootstrap

> Bootstrapping in Netty is the process by which you configure your Netty application. You use a bootstrap when you need to connect a client to some host and port, or bind a server to a given port.
>
> netty使用Bootstrap来进行netty应用程序的配置.用户使用Bootstrap来把客户端连接到主机和端口,或者把服务端绑定到一个本地端口.

**Bootstrap和ServerBootstrap的异同**

|      相同/不同       |     Bootstrap      | ServerBootstrap |
| :------------------: | :----------------: | :-------------: |
|         职责         | 连接远程主机和端口 |  绑定本地端口   |
| EventLoopGroup的数量 |         1          |        2        |

#### ChannelInboundHandler

> The ChannelInboundHandler receives messages which you can process and decide what to do with it. In other words, the business logic of your application typically lives in a ChannelInboundHandler.
>
> ChannelInboundHandler用来接收业务数据,换句话说,用户的数据业务处理逻辑一般都写在一个ChannelInboundHandler里

#### ChannelInitializer

> This(Configuring Handlers) is also done via different types of handlers but to configure these handlers Netty has what is known as an ChannelInitializer. The role of the ChannelInitializer is to add ChannelHandler implementations to whats called the ChannelPipeline.
>
> 要配置使用的Handler,有几种不同的办法,不过netty提供了ChannelInitializer来做这件事,ChannelInitializer的角色,就是负责将ChannelHandler的实现类添加到ChannelPipeline中

#### ChannelPipeline

> The ChannelPipeline is closely related to whats known as the EventLoop and EventLoopGroup because all three of them are related to events or event handling.
>
> ChannelPipeline和EventLoop,EventLoopGroup有着紧密的关联,因为这三个对象都和事件和事件处理有关.

#### EventLoopGroup

> An EventLoop's purpose in the application is to process IO operations for a Channel. A single EventLoop will typically handle events for multiple Channels.
>
> 应用程序中EventLoop的职责是处理Channel的IO操作,一个EventLoop可以同时处理多个Channel的事件.
>
> The EventLoop is always bound to a single Thread that never changed during its life time.
>
> EventLoop绑定到一个线程,且在它的生命周期之内都不会改变这一点.**这就是EventLoop和线程的关系**
>
> This means keep the EventLoop too busy in one Channel will disallow to process the other Channels that are bound to the same EventLoop. This is one of the reasons why you MUST NOT block the EventLoop in all cases.
>
> 这意味着,如果一个EventLoop过于忙碌,它就会无法处理绑定到自己的其他channel.这就是为什么用户应该尽量避免阻塞EventLoop的原因.
>
> The EventLoopGroup itself may contain more then one EventLoop and can be used to obtain an EventLoop.
>
> EventLoopGroup自身维护着复数的EventLoop,可以通过它来获取一个EventLoop

#### Channel

> A Channel is a representation of a socket connection or some component capable of performing IO operations, hence why it is managed by the EventLoop whose job it is to process IO.
>
> Channel可以表示一个Socket连接,或者一个能进行IO操作的组件,因此使用EventLoop来管理Channel,因为EventLoop的职责正是处理IO操作.
>
> When a channel is registered, Netty binds that channel to a single EventLoop (and so to a single thread) for the lifetime of that Channel. This is why your application doesnt need to synchronize on Netty IO operations because all IO for a given Channel will always be performed by the same thread.
>
> 在一个channel注册后,netty会将它绑定到一个EventLoop(因此也就绑定了一个线程),这个绑定关系在channel的生命周期之内都不会改变.这就是为什么使用netty的应用程序不需要在netty IO上进行同步操作,因为一个channel的所有IO操作都只会在单线程上执行.

#### ChannelFuture

> A ChannelFuture is a special java.util.concurrent.Future, which allows you to register ChannnelFutureListeners to the ChannelFuture. Those ChannelFutureListeners will get notified once the operation (which was triggered by the method call) is complete.
>
> ChannelFutual是一种java.util.concurrent.Future的实现,允许用户向自身注册复数的ChannelFutureListener.当操作(被方法调用激活)完成时,这些ChannelFutureListener会收到通知.
>
> So basically a ChannelFuture is a placeholder for a result of an operation that is executed in the future.
>
> 基本上,ChannelFuture就是一个会在未来执行(即异步调用)的操作的返回结果的占位符
>
> The only thing you can be sure of is that it will be executed and all operations that return a ChannelFuture and belong to the same Channel will be executed in the correct order, which is the same order as you executed the methods.
>
> 唯一能确定的是,ChannelFuture一定会被执行,且属于一个Channel的所有ChannelFuture,都会按照用户程序中声明的顺序被正确地执行

#### Netty中的所有IO操作都是异步的

> you cant know if an operation was successful or not after it returns, but need to be able to check later for success or have some kind of ways to register a listener which is notified.
>
> 只有进行自主轮询,或注册用来监听调用结果的监听器,用户才能得知操作的返回结果,否则用户无从得知操作是成功还是失败.
>
> To rectify this, Netty uses Futures and ChannelFutures. This future can be used to register a listener, which will be notified when an operation has either failed or completed successfully.
>
> 为了修正上述的缺点,Netty使用了Future和ChannelFuture.这些Future可以用来注册监听器,监听器在操作成功或失败时会得到通知.
>
> P38 last 3 lines

#### ChannelHandler

> Handlers themselves depend upon the aforementioned ChannelPipeline to prescribe their order of execution.
>
> ChannelHandler和ChannelPipeline是循环依赖的关系.Handler本身依赖于前述的ChannelPipeline,通过ChannelPipeline来描述handlers的执行顺序.
>
> Both ChannelInboundHandler and ChannelOutboundHandler can be mixed into the same ChannelPipeline.
>
> ChannelInboundHandler和ChannelOutboundHandler可以被混用在同一个ChannelPipeline中.
>
> A ChannelHandler can be thought of as any piece of code that processes data coming and going through the ChannelPipeline.
>
> 前文:因为ChannelHandler是如此通用,很难去具体描述它们.只要是用来处理通过ChannelPipeline的数据的代码块,就可以被认为是一个ChannelHandler.
>
> Data is said to be outbound if the expected flow is from the user application to the remote peer. Conversely, data is inbound if it is coming from the remote peer to the user application.
>
> 数据如果是从用户应用(即netty应用程序)流向远端节点,则可认为是outbound的;相对的,如果数据从远端流向netty应用程序,则可被认为是inbound的.
>
> The order in which ChannelHandlers were added determines the order in which they would have manipulated the data.
>
> ChannelHandlers被添加的顺序决定了它们处理数据的顺序.
>
> Once a ChannelHandler is added to a ChannelPipeline it also gets what's called a ChannelHandlerContext. Typically it is safe to get a reference to this object and keep it around. This is not true when a datagram protocol is used such as UDP.
>
> 当一个ChannelHandler被添加到ChannelPipeline中后,它就会得到一个称为ChannelHandlerContext的上下文对象.**通常使用这个ChannelHandlerContext对象都是安全的,但是使用UDP协议时除外**.
>
> there are two ways of sending messages in Netty. You can write directly to the channel or write to the ChannelHandlerContext object. The main difference between the two is that writing to the channel directly causes the message to start from the tail of the ChannelPipeline whereas writing to the context object causes the message to start from the next handler in the ChannelPipeline.
>
> 在Netty中有两种发送消息的方式.一个是向channel中写入,另一个是向ChannelHandlerContext对象中写入.这两种方式的主要区别在于,向前者中写入会导致消息从ChannelPipeline的outbound handler尾部开始发送,而向ChannelHandlerContext中写入,消息则会在ChannelPipeline中的下一个handler开始发送.

作者建议,如果想实现自己的ChannelHandler,最好继承如下Adapter之一,可以使开发工作简单一些

- ChannelHandlerAdapter
- ChannelInboundHandlerAdapter
- ChannelOutboundHandlerAdapter
- ChannelDuplexHandler

##### **ChannelHandler and Servlet similarities**

是不是感觉ChannelHandler和Servlet很像?确实很像,一个ChannelHandler可以对数据做一些操作,也可以不做,然后传递给ChannelPipeline中的下一个handler,然后下一个handler也可以做继续转发数据.

##### ChannelHandler中的阻塞操作

> As said before you MUST NOT block the IO Thread at all.
>
> 用户必须尽一切可能避免阻塞IO线程.
>
> Netty allows to specify an EventExecutorGroup when adding ChannelHandlers tot he ChannelPipeline. This EventExecutorGroup will then be used to obtain an EventExecutor and this EventExecutor will execute all the methods of the ChannelHandler. The EventExecutor here will use a different Thread then the one that is used for the IO and thus free up the EventLoop.
>
> 如果必须要进行IO操作也是有办法的.
>
> Netty允许在添加ChannelHandler到ChannelPipeline时,指定一个EventExecutorGroup.EventExecutorGroup被用来获取一个EventExecutor,这个EventExecutor会使用不同的线程,去执行ChannelHandler中所有的方法,因此就从IO操作中释放了EventLoop所绑定的线程.

## 自定义编码解码器

要使用自定义的编码和解码器,主要包含如下几个步骤

> - 自定义协议数据格式
    >   - MyProtocol,前4字节用来存放int类型数据,用来描述发送的有效数据长度;后面才跟着是业务数据
> - 自定义编码器(Encoder)
    >   - 并注册到客户端ChannelInitializer中
> - 自定义解码器(Decoder)
    >   - 并注册到服务端ChannelInitializer中

## Hessian序列化

JDK自带的序列化使用很简便,但是性能比较差,使用Hessian替代.

使用Hessian进行序列化很简单,只需引入[模板代码](https://gist.github.com/LukeHatton/7c4cfa65c0aef27bc2921957acbc52c5)即可


## netty实践

- 用netty实现一个通讯系统√
- 实现客户端到服务端通信的编码解码✅

    - 之前遇到一个问题,客户端也能连接服务端,但是就是无法发送数据.事后发现,是在MyRPCClient里创建ChannelInitializer的时候写错了,写成了服务端用的ChannelInitializer
- 用netty实现一个简单的服务器，其实现步骤如下✅
    1. 解析客户端收到的请求URL:HttpRequestDecoder
    2. 解析客户端的请求数据体:HttpObjectAggregator
    3. 对客户端响应进行编码:HttpResponseEncoder
    4. 可能需要处理文件:ChunkedWriteHandler
    5. 对应的业务流程处理:CustomChannelHandler
- 用netty实现一个RPC框架