package com.example.netty_2_3.protocol;

import com.example.netty_2_3.entity.Student;
import com.example.netty_2_3.util.HessianSerializer;
import com.example.netty_2_3.util.HessianSerializerSingleton;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * ClassName: HessianDecoder
 * <p>
 * Description:Hessian解码器
 * <p>
 * Author: Zhao Li
 * <p>
 * Date: 10/29/2021 22:59
 * <p>
 * History:
 */
public class HessianDecoder extends ByteToMessageDecoder {

    private final HessianSerializer hessianSerializer = HessianSerializerSingleton.INSTANCE.getInstance();

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        /* ================itcast code================= */
        //复制一份ByteBuf数据，轻复制，非完全拷贝
        //避免出现异常：did not read anything but decoded a message
        //Netty检测没有读取任何字节就会抛出该异常
        ByteBuf in2 = in.retainedDuplicate();
        byte[] dst;
        if (in2.hasArray()) {//堆缓冲区模式
            dst = in2.array();
        } else {
            dst = new byte[in2.readableBytes()];
            in2.getBytes(in2.readerIndex(), dst);
        }
        in.skipBytes(in.readableBytes());                                           //跳过所有的字节，表示已经读取过了
        Object obj = hessianSerializer.deserialize(dst, Student.class);             //反序列化
        out.add(obj);
    }
}
