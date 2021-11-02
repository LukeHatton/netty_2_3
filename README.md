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

#### ChannelInboundHandler

> The ChannelInboundHandler receives messages which you can process and decide what to do with it.
>
> In other words, the business logic of your application typically lives in a ChannelInboundHandler.

#### ChannelInitializer

> ChannelInitializer. The role of the ChannelInitializer is to add ChannelHandler implementations to whats called the ChannelPipeline.

#### ChannelPipeline

> The ChannelPipeline is closely related to whats known as the EventLoop and EventLoopGroup because all three of them are related to events or event handling.

#### EventLoopGroup

> An EventLoops purpose in the application is to process IO operations for a Channel. A single EventLoop will typically handle events for multiple Channels.
>
> The EventLoopGroup itself may contain more then one EventLoop and can be used to obtain an EventLoop.

#### Channel

> A Channel is a representation of a socket connection or some component capable of performing IO operations, hence why it is managed by the EventLoop whose job it is to process IO.

#### Netty中的所有IO操作都是异步的

> you cant know if an operation was successful or not after it returns, but need to be able to check later for success or have some kind of ways to register a listener which is notified.
>
> To rectify this, Netty uses Futures and ChannelFutures. This future can be used to register a listener, which will be notified when an operation has either failed or completed successfully.
>
> P38 last 3 lines

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

- 实现客户端到服务端通信的编码解码

    - 之前遇到一个问题,客户端也能连接服务端,但是就是无法发送数据.事后发现,是在MyRPCClient里创建ChannelInitializer的时候写错了,写成了服务端用的ChannelInitializer