package com.example.netty_2_3.server;

import com.example.netty_2_3.protocol.MyDecoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import lombok.extern.slf4j.Slf4j;

/**
 * Package: com.example.netty_2_3.server
 * <p>
 * Description:自定义channel初始化器,初始化channel.初始化channel主要是为channel添加handler处理链
 * <p>
 * User: lizhao 2021/10/27
 * <p>
 */
@Slf4j
public class MyChannelInitializer extends ChannelInitializer<SocketChannel> {

    /**
     * 添加channel handler链
     *
     * @param ch 要为之添加handler的channel对象
     * @throws Exception 异常
     */
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        log.info("=====> 尝试添加channel处理器到pipeline：【{}】...", MyChannelHandler.class.getName());
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new MyChannelHandler());
        log.info("=====> 自定义channel处理器：【{}】 成功添加到channel pipeline中！", MyChannelHandler.class.getName());
        pipeline.addLast(new MyDecoder());
        log.info("=====> 自定义channel处理器：【{}】 成功添加到channel pipeline中！", MyDecoder.class.getName());
    }
}
