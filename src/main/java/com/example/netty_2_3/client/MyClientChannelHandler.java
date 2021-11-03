package com.example.netty_2_3.client;

import com.example.netty_2_3.entity.Student;
import com.example.netty_2_3.util.HessianSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.Random;
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

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
        System.out.println("接收到服务端的消息：" + msg.toString(CharsetUtil.UTF_8));
        this.channelActive(ctx);
    }

    /* channel启动行为：向服务端发送数据 */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        /* ================获取数据================= */
        Scanner scanner = new Scanner(System.in);
        System.out.println("按任意键以继续...");
        scanner.nextLine();
        Random random = new Random();
        Student student = new Student(random.nextInt(20), random.nextInt(2), "personName:" + LocalDateTime.now());

        ctx.writeAndFlush(student);                              //因为已经是字节数据了,不需要再指定编码格式
    }

    /* 异常处理 */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();                                    //出现异常，直接断开连接，释放资源
        log.error("Exception occurs!", cause);          //日志记录异常信息
    }
}
