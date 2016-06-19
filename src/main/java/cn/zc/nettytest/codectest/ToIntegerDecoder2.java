package cn.zc.nettytest.codectest;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;

/**
 * 
 * @author zero
 *
 *
 *         1.实现继承自 ReplayingDecoder 用于将字节解码为消息 
 *         2.从入站 ByteBuf 读取整型，并添加到解码消息的 List中
 */
public class ToIntegerDecoder2 extends ReplayingDecoder<Void> { // 1

	@Override
	public void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		out.add(in.readInt()); // 2
	}
}