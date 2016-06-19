package cn.zc.nettytest.codectest;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.TooLongFrameException;

/**
 * 
 * @author zero
 *
 *         1.实现继承 ByteToMessageDecoder 来将字节解码为消息 
 *         2.检测缓冲区数据是否大于 MAX_FRAME_SIZE
 *         3.忽略所有可读的字节，并抛出 TooLongFrameException 来通知 ChannelPipeline 中的
 *         ChannelHandler 这个帧数据超长
 */
public class SafeByteToMessageDecoder extends ByteToMessageDecoder { // 1
	private static final int MAX_FRAME_SIZE = 1024;

	@Override
	public void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		int readable = in.readableBytes();
		if (readable > MAX_FRAME_SIZE) { // 2
			in.skipBytes(readable); // 3
			throw new TooLongFrameException("Frame too big!");
		}
		// do something
	}
}