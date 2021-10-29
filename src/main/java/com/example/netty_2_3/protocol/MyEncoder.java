package com.example.netty_2_3.protocol;

import com.example.netty_2_3.protocol.MyProtocol;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * ClassName: MyEncoder
 * <p>
 * Description:自定义客户端加密协议
 * <p>
 * Author: Zhao Li
 * <p>
 * Date: 10/28/2021 21:06
 * <p>
 * History:
 */
public class MyEncoder extends MessageToByteEncoder<MyProtocol> {

    /**
     * 自定义编码
     *
     * @param channelHandlerContext 自定义的{@link MessageToByteEncoder}所属的channelHandler上下文对象
     * @param myProtocol            自定义协议
     * @param byteBuf               经过编码的信息要写入的{@link ByteBuf}对象
     * @throws Exception exception
     */
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, MyProtocol myProtocol, ByteBuf byteBuf) throws Exception {
        byteBuf.writeInt(myProtocol.getLength());          //写入:数据长度(字节)
        byteBuf.writeBytes(myProtocol.getData());            //写入:字节数据
        //可能并不需要下面这行代码,但是暂时先留着
        // channelHandlerContext.writeAndFlush(byteBuf);
    }
}
