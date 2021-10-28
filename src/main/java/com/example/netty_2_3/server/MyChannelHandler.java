package com.example.netty_2_3.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * Package: com.example.netty_2_3.server
 * <p>
 * 自定义ChannelHandler
 * <p>
 * User: lizhao 2021/10/27
 * <p>
 */
@Slf4j
public class MyChannelHandler extends ChannelInboundHandlerAdapter {

    /* 读取客户端数据 */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        //ByteBuf默认分配区：PooledUnsafeDirectByteBuf
        //添加系统变量和Bootstrap属性后的缓冲分配区：InstrumentUnpooledUnsafeDirectByteBuf
        ByteBuf byteBuf = (ByteBuf) msg;
        String message = byteBuf.toString(CharsetUtil.UTF_8);
        System.out.println("==>what does the client say: " + message);
        //write()方法不会立刻将缓冲中的数据写回给channel连接
        ctx.write(Unpooled.copiedBuffer("echo message: " + message + "\r\n", CharsetUtil.UTF_8));
        //需要手动释放ByteBuf
        ReferenceCountUtil.release(msg);
    }

    /* 给客户端响应数据 */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        //writeAndFlush()方法会立刻将缓冲中的数据写回给channel连接
        ctx.writeAndFlush(Unpooled.copiedBuffer("SUCCESS\r\n", CharsetUtil.UTF_8));
    }

    /* 异常处理 */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();                                    //出现异常，直接断开连接，释放资源
        log.error("Exception occurs!", cause);          //日志记录异常信息
    }
}
