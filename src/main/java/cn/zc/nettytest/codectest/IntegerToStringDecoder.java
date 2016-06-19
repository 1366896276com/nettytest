package cn.zc.nettytest.codectest;

import java.util.List;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

/**
 * 
 * @author zero
 *
 *
 *         1.实现继承自 MessageToMessageDecoder 
 *         2.通过 String.valueOf() 转换 Integer消息字符串
 */
public class IntegerToStringDecoder extends MessageToMessageDecoder<Integer> { // 1

	@Override
	public void decode(ChannelHandlerContext ctx, Integer msg, List<Object> out) throws Exception {
		out.add(String.valueOf(msg)); // 2
	}
}
