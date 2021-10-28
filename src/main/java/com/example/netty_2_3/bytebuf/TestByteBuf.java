package com.example.netty_2_3.bytebuf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.CharsetUtil;

/**
 * Package: com.example.netty_2_3.bytebuf
 * <p>
 * Description:练习ByteBuf API的使用的演示demo
 * <p>
 * User: lizhao 2021/10/27
 * <p>
 */
public class TestByteBuf {

    /* ByteBuf：读 */
    public void testRead() {
        ByteBuf byteBuf = Unpooled.copiedBuffer("hello world!", CharsetUtil.UTF_8);
        System.out.println("byteBuf的容量：" + byteBuf.capacity());
        System.out.println("byteBuf的可读容量：" + byteBuf.readableBytes());
        System.out.println("byteBuf的可写容量：" + byteBuf.writableBytes());
        System.out.println();

        /* 读取方法1：通过移动readerIndex进行读取 */
        System.out.println("==>方法1读取：readerIndex");
        while (byteBuf.isReadable()) {
            System.out.println((char) byteBuf.readByte());
        }
        System.out.println();

        /* 通过下标读取会和用索引读冲突，因为上面读完了后就没有readableByte了 */
        /* 读取方法2：通过下标读取 */
        System.out.println("==>方法2读取：下标");
        for (int i = 0; i < byteBuf.readableBytes(); i++) {
            System.out.println((char) byteBuf.readByte());
        }
        System.out.println();

        /* 读取方法3：转换成byte[]读取 */
        System.out.println("==>方法3读取：转换成byte[]");
        byte[] array = byteBuf.array();
        for (byte b : array) {
            System.out.println((char) b);
        }
    }

    /* ByteBuf：写 */
    public void testWrite(){
        /* 初始化容量：10 最大容量：20 */
        ByteBuf byteBuf = Unpooled.buffer(10, 20);

        System.out.println();
        System.out.println("byteBuf的容量：" + byteBuf.capacity());
        System.out.println("byteBuf的可读容量：" + byteBuf.readableBytes());
        System.out.println("byteBuf的可写容量：" + byteBuf.writableBytes());
        System.out.println();

        /* 向byteBuf中写数据 */
        for (int i = 0; i < 5; i++) {
            byteBuf.writeInt(i);        //写入int数据，一个int占用4字节
        }
        System.out.println("ok");

        System.out.println("byteBuf的容量：" + byteBuf.capacity());
        System.out.println("byteBuf的可读容量：" + byteBuf.readableBytes());
        System.out.println("byteBuf的可写容量：" + byteBuf.writableBytes());
        System.out.println();

        while (byteBuf.isReadable()) {
            System.out.println(byteBuf.readInt());
        }

    }
}
