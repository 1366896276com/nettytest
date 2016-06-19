package cn.zc.nettytest.serializabletest;

import com.google.protobuf.MessageLite;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;

/**
 * Using Google Protobuf
 * @author zero
 *
 *
 *         1.添加 ProtobufVarint32FrameDecoder 用来分割帧 
 *         2.添加 ProtobufEncoder 用来处理消息的编码
 *         3.添加ProtobufDecoder 用来处理消息的解码 
 *         4.添加 ObjectHandler 用来处理解码了的消息
 */
public class ProtoBufInitializer extends ChannelInitializer<Channel> {

	private final MessageLite lite;

	public ProtoBufInitializer(MessageLite lite) {
		this.lite = lite;
	}

	@Override
	protected void initChannel(Channel ch) throws Exception {
		ChannelPipeline pipeline = ch.pipeline();
		pipeline.addLast(new ProtobufVarint32FrameDecoder());
		pipeline.addLast(new ProtobufEncoder());
		pipeline.addLast(new ProtobufDecoder(lite));
		pipeline.addLast(new ObjectHandler());
	}

	public static final class ObjectHandler extends SimpleChannelInboundHandler<Object> {
		@Override
		public void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
			// Do something with the object
		}
	}
}