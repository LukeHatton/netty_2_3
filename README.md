[TOC]

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

```java
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

## netty实践

用netty实现一个通讯系统