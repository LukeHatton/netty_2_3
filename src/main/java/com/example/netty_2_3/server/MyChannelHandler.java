package com.example.netty_2_3.server;

import com.example.netty_2_3.protocol.MyProtocol;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;
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
public class MyChannelHandler extends SimpleChannelInboundHandler<MyProtocol> {

    /* 读取客户端数据 */
    /*使用自定义协议,不需要这个方法了
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        //ByteBuf默认分配区：PooledUnsafeDirectByteBuf
        //添加系统变量和Bootstrap属性后的缓冲分配区：InstrumentUnpooledUnsafeDirectByteBuf
        ByteBuf byteBuf = (ByteBuf) msg;
        String message = byteBuf.toString(CharsetUtil.UTF_8);
        System.out.println("==>what does the client say: " + message);
        //write()方法不会立刻将缓冲中的数据写回给channel连接
        ctx.write(Unpooled.copiedBuffer("echo message: " + message + "\r\n", CharsetUtil.UTF_8));
        //如果是继承的ChannelInboundHandlerAdapter,就需要手动释放ByteBuf
        //如果是继承的SimpleChannelInboundHandler,就不需要手动释放
        // ReferenceCountUtil.release(msg);
    }*/

    /* 读MyProtocol类型数据 */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MyProtocol msg) throws Exception {
        System.out.println("==>received data from client: " + msg);
        ctx.writeAndFlush(Unpooled.copiedBuffer("SUCCESS", CharsetUtil.UTF_8));
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
