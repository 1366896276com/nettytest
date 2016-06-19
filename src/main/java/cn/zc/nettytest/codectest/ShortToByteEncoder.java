package cn.zc.nettytest.codectest;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * 
 * @author zero
 *
 *         1.实现继承自 MessageToByteEncoder 
 *         2.写 Short 到 ByteBuf
 */
public class ShortToByteEncoder extends MessageToByteEncoder<Short> { // 1
	@Override
	public void encode(ChannelHandlerContext ctx, Short msg, ByteBuf out) throws Exception {
		out.writeShort(msg); // 2
	}
}
