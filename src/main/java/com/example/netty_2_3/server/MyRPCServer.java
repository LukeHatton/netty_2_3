package com.example.netty_2_3.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * ClassName: MyRPCServer
 * <p>
 * Description: 黑马课程视频《Netty通信技术进阶（上）》，根据里面描述的Netty工作线程模型，创建演示demo
 * <p>
 * Author: Zhao Li
 * <p>
 * Date: 10/26/2021 23:03
 * <p>
 * History:
 */
public class MyRPCServer {

    /* 开启netty server服务 */
    public void start(int port) {
        /* ================创建事件轮询器================= */
        //创建线程池Boss，进行事件轮询，只用来获取客户端连接
        EventLoopGroup parentGroup = new NioEventLoopGroup(1);      //parentGroup线程数：1
        //创建现程池worker，进行事件轮询，用来处理请求
        EventLoopGroup childGroup = new NioEventLoopGroup(2);       //childGroup线程数：2


        //创建服务对象
        ServerBootstrap bootstrap = new ServerBootstrap();
        //服务对象绑定端口
        try {
            ChannelFuture future = bootstrap
                    .channel(NioServerSocketChannel.class)                   //指定通道接受的协议:NIO-TCP
                    .group(parentGroup, childGroup)                          //两个轮询组:parentGroup处理连接,childGroup处理请求
                    .childHandler(new ChannelInboundHandlerAdapter() {       //TODO 处理入栈请求

                    })
                    .bind(port).sync();                                      //sync()启动服务，监听对应端口
            /*
            通过channel()方法得到channel对象;
            调用closeFuture()方法,当channel关闭时返回ChannelFuture对象;
            调用sync(),等待,直到future对象结束
             */
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            //关闭线程池
            parentGroup.shutdownGracefully();
            childGroup.shutdownGracefully();
        }
    }
}
