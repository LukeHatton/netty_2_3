package com.example.netty_2_3.server;

import com.example.netty_2_3.entity.Student;
import com.example.netty_2_3.protocol.MyProtocol;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;

/**
 * Package: com.example.netty_2_3.server
 * <p>
 * 自定义ChannelHandler
 * <p>
 * User: lizhao 2021/10/27
 * <p>
 */
@Slf4j
public class MyChannelHandler extends SimpleChannelInboundHandler<Student> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Student msg) throws Exception {
        //获取到user对象
        System.out.println(msg);
        ctx.writeAndFlush(Unpooled.copiedBuffer("SUCCESS", CharsetUtil.UTF_8));
    }

    /* 数据读取完成后的操作：给客户端响应数据 */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        byte[] data = "==>SUCCESS from read complete".getBytes(StandardCharsets.UTF_8);
        MyProtocol myProtocol = new MyProtocol(data.length, data);
        ctx.writeAndFlush(myProtocol);
    }

    /* 异常处理 */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();                                    //出现异常，直接断开连接，释放资源
        log.error("Exception occurs!", cause);          //日志记录异常信息
    }
}
