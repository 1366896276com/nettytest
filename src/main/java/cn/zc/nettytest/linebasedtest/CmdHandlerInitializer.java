package cn.zc.nettytest.linebasedtest;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.LineBasedFrameDecoder;

/**
 * 
 * @author zero
 *
 *         1.添加一个 CmdDecoder 到管道；将提取 Cmd 对象和转发到在管道中的下一个处理器 
 *         2.添加 CmdHandler 将接收和处理Cmd 对象 
 *         3.命令也是 POJO 
 *         4.super.decode() 通过结束分隔从 ByteBuf 提取帧 
 *         5.frame 是空时，则返回null 
 *         6.找到第一个空字符的索引。首先是它的命令名；接下来是参数的顺序 
 *         7.从帧先于索引以及它之后的片段中实例化一个新的 Cmd 对象
 *         8.处理通过管道的 Cmd 对象
 */
public class CmdHandlerInitializer extends ChannelInitializer<Channel> {

	@Override
	protected void initChannel(Channel ch) throws Exception {
		ChannelPipeline pipeline = ch.pipeline();
		pipeline.addLast(new CmdDecoder(65 * 1024));// 1
		pipeline.addLast(new CmdHandler()); // 2
	}

	public static final class Cmd { // 3
		private final ByteBuf name;
		private final ByteBuf args;

		public Cmd(ByteBuf name, ByteBuf args) {
			this.name = name;
			this.args = args;
		}

		public ByteBuf name() {
			return name;
		}

		public ByteBuf args() {
			return args;
		}
	}

	public static final class CmdDecoder extends LineBasedFrameDecoder {
		public CmdDecoder(int maxLength) {
			super(maxLength);
		}

		@Override
		protected Object decode(ChannelHandlerContext ctx, ByteBuf buffer) throws Exception {
			ByteBuf frame = (ByteBuf) super.decode(ctx, buffer); // 4
			if (frame == null) {
				return null; // 5
			}
			int index = frame.indexOf(frame.readerIndex(), frame.writerIndex(), (byte) ' '); // 6
			return new Cmd(frame.slice(frame.readerIndex(), index), frame.slice(index + 1, frame.writerIndex())); // 7
		}
	}

	public static final class CmdHandler extends SimpleChannelInboundHandler<Cmd> {
		@Override
		public void channelRead0(ChannelHandlerContext ctx, Cmd msg) throws Exception {
			// Do something with the command //8
		}
	}
}
