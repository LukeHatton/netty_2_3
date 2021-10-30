package com.example.netty_2_3.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;

/**
 * ClassName: MyRPCServer
 * <p>
 * Description: 黑马课程视频《Netty通信技术进阶（上）》,根据里面描述的Netty工作线程模型,创建演示demo
 * <p>
 * Author: Zhao Li
 * <p>
 * Date: 10/26/2021 23:03
 * <p>
 * History:
 */
@Slf4j
public class MyRPCServer {

    /* 开启netty server服务 */
    @SneakyThrows
    public void start(int port) {
        log.info("=====> 本机ip：{} 监听端口：{}", InetAddress.getLocalHost(), port);
        log.info("=====> server尝试启动中...");
        /* ================1.创建事件轮询器================= */
        //创建线程池Boss,进行事件轮询,只用来获取客户端连接
        @Cleanup("shutdownGracefully") EventLoopGroup parentGroup = new NioEventLoopGroup(1);      //parentGroup线程数：1
        //创建现程池worker,进行事件轮询,用来处理请求
        @Cleanup("shutdownGracefully") EventLoopGroup childGroup = new NioEventLoopGroup(2);       //childGroup线程数：2

        /* ================创建handler并启动================= */
        //2.创建服务对象
        ServerBootstrap bootstrap = new ServerBootstrap();
        //ByteBuf使用堆缓冲区，还需要设置Bootstrap属性
        bootstrap.childOption(ChannelOption.ALLOCATOR, UnpooledByteBufAllocator.DEFAULT);
        //3.服务对象绑定端口
        ChannelFuture future = bootstrap
                .channel(NioServerSocketChannel.class)                   //因为数据可以有多种协议,需要指定通道接受的协议:NIO-TCP
                .group(parentGroup, childGroup)                          //两个轮询组:parentGroup处理连接,childGroup处理请求
                .childHandler(new MyChannelInitializer())                //使用支持自定义channel处理链的ChannelInitializer
                .bind(port).sync();                                      //sync()启动服务,监听对应端口

        log.info("=====> server启动成功！");
            /*
            通过channel()方法得到channel对象;
            调用closeFuture()方法,线程在此阻塞,等待客户端连接,当channel关闭时返回ChannelFuture对象;
            调用sync(),等待,直到future对象结束
             */
        future.channel().closeFuture().sync();
    }
}
