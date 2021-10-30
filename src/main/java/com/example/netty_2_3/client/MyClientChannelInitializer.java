package com.example.netty_2_3.client;

import com.example.netty_2_3.protocol.HessianEncoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import lombok.extern.slf4j.Slf4j;

/**
 * Package: com.example.netty_2_3.client
 * <p>
 * Description:自定义channel初始化器,初始化channel.初始化channel主要是为channel添加handler处理链
 * <p>
 * User: lizhao 2021/10/29
 * <p>
 */
@Slf4j
public class MyClientChannelInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        log.info("=====> Pipeline Initializer is working ==> 【{}】", MyClientChannelInitializer.class.getSimpleName());
        ChannelPipeline pipeline = ch.pipeline();
        //因为没有实现自动判别数据类型,所以无法对服务端发来的消息进行反序列化
        // pipeline.addLast(new HessianDecoder());
        pipeline.addLast(new HessianEncoder());
        log.info("=====> adding channel handler:【{}】 SUCCESS!", HessianEncoder.class.getName());
        pipeline.addLast(new MyClientChannelHandler());
        log.info("=====> adding channel handler:【{}】 SUCCESS!", MyClientChannelHandler.class.getName());
    }
}
