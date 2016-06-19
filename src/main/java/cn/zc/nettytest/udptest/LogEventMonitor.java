package cn.zc.nettytest.udptest;

import java.net.InetSocketAddress;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;

/**
 * 
 * @author zero
 *
 *         1.引导 NioDatagramChannel。设置 SO_BROADCAST socket 选项。
 *         2. 添加 ChannelHandler 到ChannelPipeline 
 *         
 *         3.绑定的通道。注意,在使用 DatagramChannel 是没有连接，因为这些 无连接 
 *         4.构建一个新的LogEventMonitor
 *         
 * 
 */
public class LogEventMonitor {

	private final Bootstrap bootstrap;
	private final EventLoopGroup group;

	public LogEventMonitor(InetSocketAddress address) {
		group = new NioEventLoopGroup();
		bootstrap = new Bootstrap();
		bootstrap.group(group) // 1
				.channel(NioDatagramChannel.class).option(ChannelOption.SO_BROADCAST, true)
				.handler(new ChannelInitializer<Channel>() {
					@Override
					protected void initChannel(Channel channel) throws Exception {
						ChannelPipeline pipeline = channel.pipeline();
						pipeline.addLast(new LogEventDecoder()); // 2
						pipeline.addLast(new LogEventHandler());
					}
				}).localAddress(address);

	}

	public Channel bind() {
		return bootstrap.bind().syncUninterruptibly().channel(); // 3
	}

	public void stop() {
		group.shutdownGracefully();
	}

	public static void main(String[] args) throws Exception {
		if (args.length != 1) {
			//throw new IllegalArgumentException("Usage: LogEventMonitor <port>");
			args=new String[1];
			args[0]="20080";
		}
		LogEventMonitor monitor = new LogEventMonitor(new InetSocketAddress(Integer.parseInt(args[0]))); // 4
		try {
			Channel channel = monitor.bind();
			System.out.println("LogEventMonitor running");

			channel.closeFuture().await();
		} finally {
			monitor.stop();
		}
	}
}
