package com.example.netty_2_3.server;

import com.example.netty_2_3.protocol.HessianDecoder;
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
        log.info("=====> Pipeline Initializer is working ==> 【{}】", MyChannelInitializer.class.getName());
        ChannelPipeline pipeline = ch.pipeline();
        //不需要编码器,因为客户端无法解析传输的数据(返回的数据类型不是Student,客户端无法反序列化)
        // pipeline.addLast(new HessianEncoder());
        pipeline.addLast(new HessianDecoder());
        log.info("=====> adding channel handler:【{}】 SUCCESS!", HessianDecoder.class.getName());
        pipeline.addLast(new MyChannelHandler());
        log.info("=====> adding channel handler:【{}】 SUCCESS!", MyChannelHandler.class.getName());
    }
}
