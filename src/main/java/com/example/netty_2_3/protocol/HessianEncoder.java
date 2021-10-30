package com.example.netty_2_3.protocol;

import com.example.netty_2_3.entity.Student;
import com.example.netty_2_3.util.HessianSerializer;
import com.example.netty_2_3.util.HessianSerializerSingleton;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * ClassName: HessianEncoder
 * <p>
 * Description:Hessian编码器
 * <p>
 * Author: Zhao Li
 * <p>
 * Date: 10/29/2021 22:45
 * <p>
 * History:
 */
public class HessianEncoder extends MessageToByteEncoder<Student> {

    private final HessianSerializer hessianSerializer = HessianSerializerSingleton.INSTANCE.getInstance();

    @Override
    protected void encode(ChannelHandlerContext ctx, Student msg, ByteBuf out) throws Exception {
        byte[] data = hessianSerializer.serialize(msg);
        out.writeBytes(data);
    }
}
