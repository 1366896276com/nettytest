package cn.zc.nettytest.linebasedtest;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LineBasedFrameDecoder;

/**
 * 
 * 
 * @author zero
 *
 *         1.添加一个 LengthFieldBasedFrameDecoder ,用于提取基于帧编码长度8个字节的帧。 
 *         2.添加一个FrameHandler 用来处理每帧
 *         3. 处理帧数据
 */
public class LengthFieldBasedHandlerInitializer extends ChannelInitializer<Channel> {

	@Override
	protected void initChannel(Channel ch) throws Exception {
		ChannelPipeline pipeline = ch.pipeline();
		pipeline.addLast(new LengthFieldBasedFrameDecoder(1024,0,8)); // 1
		pipeline.addLast(new FrameHandler()); // 2
	}

	public static final class FrameHandler extends SimpleChannelInboundHandler<ByteBuf> {
		@Override
		public void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
			// Do something with the frame //3
		}
	}
}
