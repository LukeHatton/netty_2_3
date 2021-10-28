package com.example.netty_2_3.protocol;

import com.example.netty_2_3.protocol.MyProtocol;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;

import java.util.List;

/**
 * ClassName: MyDecoder
 * <p>
 * Description:自定义服务端解码器
 * <p>
 * Author: Zhao Li
 * <p>
 * Date: 10/28/2021 21:24
 * <p>
 * History:
 */
public class MyDecoder extends ReplayingDecoder<Void> {

    /**
     * 自定义解码
     *
     * @param ctx 自定义的{@link io.netty.handler.codec.ByteToMessageDecoder}所属的ChannelHandler所属的上下文对象
     * @param in  读取数据的{@link ByteBuf}对象
     * @param out 读取的数据要添加到的{@link List}
     * @throws Exception exception
     */
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        int length = in.readInt();
        byte[] data = new byte[length];//数据长度(字节)
        in.readBytes(data);                //读取数据到数组中

        /* ================protocol================= */
        MyProtocol myProtocol = new MyProtocol(length, data);

        out.add(myProtocol);
    }
}
