package com.example.netty_2_3.client;

import com.example.netty_2_3.protocol.MyEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.SneakyThrows;

/**
 * Package: com.example.netty_2_3.client
 * <p>
 * Description: netty客户端演示demo
 * <p>
 * User: lizhao 2021/10/27
 * <p>
 */
public class MyRPCClient {

    @SneakyThrows
    public void start(String host, int port) {
        /* ================1.创建事件轮询器================= */
        EventLoopGroup worker = new NioEventLoopGroup();

        /* ================2.创建handler并启动================= */
        try {
            Bootstrap bootstrap = new Bootstrap();
            ChannelFuture future = bootstrap
                    .group(worker)
                    .channel(NioSocketChannel.class)            //使用的协议：NIO-TCP
                    .handler(new MyEncoder())                   //使用自定义编码器
                    .handler(new MyClientChannelHandler())      //自定义客户端数据处理器
                    .connect(host, port).sync();                //连接远程主机和端口

            /* ================3.发送数据================= */
            future.channel().closeFuture().sync();
        } finally {
            worker.shutdownGracefully();
        }
    }
}
