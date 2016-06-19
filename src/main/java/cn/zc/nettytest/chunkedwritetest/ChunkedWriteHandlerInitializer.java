package cn.zc.nettytest.chunkedwritetest;

import java.io.File;
import java.io.FileInputStream;

import cn.zc.nettytest.util.SslContext;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.stream.ChunkedStream;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * 
 * @author zero
 *
 *
 *         1.添加 SslHandler 到 ChannelPipeline. 
 *         2.添加 ChunkedWriteHandler 用来处理作为ChunkedInput 传进的数据
 *         3.当连接建立时，WriteStreamHandler 开始写文件的内容
 *         4.当连接建立时，channelActive() 触发使用 ChunkedInput 来写文件的内容 (插图显示了FileInputStream;也可以使用任何 InputStream )
 */
public class ChunkedWriteHandlerInitializer extends ChannelInitializer<Channel> {
	private final File file;
	private final SslContext sslCtx;

	public ChunkedWriteHandlerInitializer(File file, SslContext sslCtx) {
		this.file = file;
		this.sslCtx = sslCtx;
	}

	@Override
	protected void initChannel(Channel ch) throws Exception {
		ChannelPipeline pipeline = ch.pipeline();
		pipeline.addLast(new SslHandler(sslCtx.newEngine(null))); // 1
		pipeline.addLast(new ChunkedWriteHandler());// 2
		pipeline.addLast(new WriteStreamHandler());// 3
	}

	public final class WriteStreamHandler extends ChannelInboundHandlerAdapter { // 4

		@Override
		public void channelActive(ChannelHandlerContext ctx) throws Exception {
			super.channelActive(ctx);
			ctx.writeAndFlush(new ChunkedStream(new FileInputStream(file)));
		}
	}
}
