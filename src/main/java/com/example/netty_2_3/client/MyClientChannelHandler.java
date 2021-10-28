package com.example.netty_2_3.client;

import com.example.netty_2_3.protocol.MyProtocol;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 * Package: com.example.netty_2_3.client
 * <p>
 * Description:自定义数据发送处理器。数据缓冲类型：ByteBuf
 * <p>
 * User: lizhao 2021/10/27
 * <p>
 */
@Slf4j
public class MyClientChannelHandler extends SimpleChannelInboundHandler<ByteBuf> {

    private static Scanner scanner = new Scanner(System.in);

    /* 获取响应数据 */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
        System.out.println("=====> server response: " + msg.toString(CharsetUtil.UTF_8));
    }

    /* channel启动行为：向服务端发送数据 */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        /* ================获取数据================= */
        if (scanner == null) scanner = new Scanner(System.in);      //这里的代码可能有问题,即使流关闭了可能也并不是null
        System.out.println("请键入要发送的数据：");
        String string = scanner.nextLine();

        /* ================protocol================= */
        byte[] data = string.getBytes(StandardCharsets.UTF_8);
        MyProtocol myProtocol = new MyProtocol(data.length, data);

        ctx.writeAndFlush(myProtocol);                              //因为已经是字节数据了,不需要再指定编码格式
    }

    /* channel读完成行为：循环向服务端发送数据 */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        channelActive(ctx);
    }

    /* 异常处理 */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();                                    //出现异常，直接断开连接，释放资源
        if (scanner != null) scanner.close();           //关闭输入流
        log.error("Exception occurs!", cause);          //日志记录异常信息
    }
}
