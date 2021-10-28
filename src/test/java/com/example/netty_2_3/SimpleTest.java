package com.example.netty_2_3;

import com.example.netty_2_3.bytebuf.TestByteBuf;
import org.junit.jupiter.api.Test;

/**
 * Package: com.example.netty_2_3
 * <p>
 * Description:简单测试类，不需要springboot环境
 * <p>
 * User: lizhao 2021/10/27
 * <p>
 */
public class SimpleTest {

    private final TestByteBuf testByteBuf = new TestByteBuf();

    /**
     * 测试ByteBuf的读取
     */
    @Test
    public void testRead() {
        testByteBuf.testRead();
    }

    @Test
    public void testWrite() {
        testByteBuf.testWrite();
    }
}
