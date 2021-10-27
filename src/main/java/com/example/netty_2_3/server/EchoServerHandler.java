package com.example.netty_2_3.server;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * ClassName: EchoServerHandler
 * Description: netty echo server demo演示类
 * Author: Zhao Li
 * Date: 10/26/2021 20:32
 * History:
 */
//Sharable注解表示此Handler可以在channel之间共享
@Sharable
public class EchoServerHandler extends ChannelInboundHandlerAdapter {

    /* 写回收到的信息.但要注意,此方法不会把数据flush到远程节点(即客户端client)上 */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("Server received: " + msg);      //控制台输出一下

        //将数据写回给服务端client
        ctx.write(msg);
    }

    /* 将所有追加(appending)的写数据信息flush给远程节点client,并在操作完成后关闭channel */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
    }

    /* 输出异常信息 */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        //出现异常,直接关闭连接
        ctx.close();                //可以有别的处理方式,不过这里为了实现方便,直接关闭连接
    }
}
