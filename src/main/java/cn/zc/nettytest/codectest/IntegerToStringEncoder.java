package cn.zc.nettytest.codectest;

import java.util.List;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

/**
 * 
 * @author zero
 *
 *         1/实现继承自 MessageToMessageEncoder 
 *         2.转 Integer 为 String，并添加到 MessageBuf
 */
public class IntegerToStringEncoder extends MessageToMessageEncoder<Integer> { // 1

	@Override
	public void encode(ChannelHandlerContext ctx, Integer msg, List<Object> out) throws Exception {
		out.add(String.valueOf(msg)); // 2
	}
}