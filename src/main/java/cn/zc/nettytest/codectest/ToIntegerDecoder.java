package cn.zc.nettytest.codectest;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

/**
 * 
 * 
 * @author zero
 *
 *
 *         1.实现继承了 ByteToMessageDecode 用于将字节解码为消息 
 *         2.检查可读的字节是否至少有4个 ( int是4个字节长度) 
 *         3.从入站 ByteBuf 读取 int ， 添加到解码消息的 List 中
 */
public class ToIntegerDecoder extends ByteToMessageDecoder { // 1

	@Override
	public void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		if (in.readableBytes() >= 4) { // 2
			out.add(in.readInt()); // 3
		}
	}
}
