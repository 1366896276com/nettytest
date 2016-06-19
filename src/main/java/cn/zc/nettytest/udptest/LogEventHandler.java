package cn.zc.nettytest.udptest;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * 
 * @author zero
 *
 *         1.继承 SimpleChannelInboundHandler 用于处理 LogEvent 消息 
 *         2.在异常时，输出消息并关闭 channel
 *         3.建立一个 StringBuilder 并构建输出 
 *         4.打印出 LogEvent 的数据
 */
public class LogEventHandler extends SimpleChannelInboundHandler<LogEvent> { // 1

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace(); // 2
		ctx.close();
	}

	@Override
	public void channelRead0(ChannelHandlerContext channelHandlerContext, LogEvent event) throws Exception {
		StringBuilder builder = new StringBuilder(); // 3
		builder.append(event.getReceivedTimestamp());
		builder.append(" [");
		builder.append(event.getSource().toString());
		builder.append("] [");
		builder.append(event.getLogfile());
		builder.append("] : ");
		builder.append(event.getMsg());

		System.out.println(builder.toString()); // 4
	}
}