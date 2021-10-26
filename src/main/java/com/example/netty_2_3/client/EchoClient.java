package com.example.netty_2_3.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;

/**
 * ClassName: EchoClient
 * Description: netty echo client demo演示类
 * Author: Zhao Li
 * Date: 10/26/2021 20:49
 * History:
 */
public class EchoClient {

    private final String host;

    private final int port;

    public EchoClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void start() throws InterruptedException {
        EventLoopGroup group = new NioEventLoopGroup();                         //指定事件轮询组,来处理客户端client事件.因为使用的是NIO传输,所以使用Nio事件轮询组(NioEventLoopGroup)
        try {
            //为client创建引导对象
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)                            //指定管道channel类型.要使用对应NIO传输的管道
                    .remoteAddress(new InetSocketAddress(host, port))           //为客户端连接设置socket绑定
                    .handler(new ChannelInitializer<SocketChannel>() {          //指定ChannelHandler-使用ChannelInitializer.在建立连接并创建管道后开始调用ChannelInitializer

                        // 将EchoClientHandler添加到channel所属的ChannelPipeline
                        // ChannelPipeline持有属于channel的所有ChannelHandler的引用
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            // ch.pipeline().addLast(new EchoClientHandler);
                        }
                    });

            ChannelFuture future = bootstrap.connect().sync();

            future.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully().sync();
        }
    }

    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println(
                    "Usage: " + EchoClient.class.getSimpleName() + "<host> <port>"
            );
            return;
        }

        //Parse options
        final String host = args[0];
        final int port = Integer.parseInt(args[1]);
    }

}
