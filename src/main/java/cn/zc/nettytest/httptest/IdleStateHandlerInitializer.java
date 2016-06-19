package cn.zc.nettytest.httptest;

import java.util.concurrent.TimeUnit;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.CharsetUtil;

/**
 * 
 * 
 * @author zero
 *
 *
 *         1.IdleStateHandler 将通过 IdleStateEvent 调用 userEventTriggered，如果连接没有接收或发送数据超过60秒钟 
 *         2.心跳发送到远端 
 *         3.发送的心跳并添加一个侦听器，如果发送操作失败将关闭连接
 *         4.事件不是一个IdleStateEvent 的话，就将它传递给下一个处理程序
 */
public class IdleStateHandlerInitializer extends ChannelInitializer<Channel> {

	@Override
	protected void initChannel(Channel ch) throws Exception {
		ChannelPipeline pipeline = ch.pipeline();
		pipeline.addLast(new IdleStateHandler(0, 0, 1, TimeUnit.SECONDS)); // 1
		pipeline.addLast(new HeartbeatHandler());
	}

	public static final class HeartbeatHandler extends ChannelInboundHandlerAdapter {
		private static final ByteBuf HEARTBEAT_SEQUENCE = Unpooled
				.unreleasableBuffer(Unpooled.copiedBuffer("HEARTBEAT", CharsetUtil.ISO_8859_1)); // 2

		@Override
		public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
			if (evt instanceof IdleStateEvent) {
				ctx.writeAndFlush(HEARTBEAT_SEQUENCE.duplicate()).addListener(ChannelFutureListener.CLOSE_ON_FAILURE); // 3
			} else {
				super.userEventTriggered(ctx, evt); // 4
			}
		}
	}
}