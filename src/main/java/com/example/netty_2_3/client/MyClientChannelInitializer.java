package com.example.netty_2_3.client;

import com.example.netty_2_3.protocol.MyDecoder;
import com.example.netty_2_3.protocol.MyEncoder;
import com.example.netty_2_3.server.MyChannelHandler;
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
        log.info("=====> 尝试添加channel处理器到pipeline：【{}】...", MyClientChannelInitializer.class.getSimpleName());
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new MyEncoder());
        log.info("=====> 自定义channel处理器：【{}】 成功添加到channel pipeline中！", MyEncoder.class.getName());
        pipeline.addLast(new MyDecoder());
        log.info("=====> 自定义channel处理器：【{}】 成功添加到channel pipeline中！", MyDecoder.class.getName());
        pipeline.addLast(new MyClientChannelHandler());
        log.info("=====> 自定义channel处理器：【{}】 成功添加到channel pipeline中！", MyClientChannelHandler.class.getName());
    }
}
