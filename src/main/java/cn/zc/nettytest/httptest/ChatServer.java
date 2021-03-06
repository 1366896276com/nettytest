package cn.zc.nettytest.httptest;

import java.net.InetSocketAddress;

import cn.zc.nettytest.echotest.EchoServer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.ImmediateEventExecutor;

/**
 * 
 * @author zero
 *
 *         1.创建 DefaultChannelGroup 用来 保存所有连接的的 WebSocket channel
 * 
 *         2.引导 服务器
 * 
 *         3.创建 ChannelInitializer
 * 
 *         4.处理服务器关闭，包括释放所有资源
 */
public class ChatServer {

	private final ChannelGroup channelGroup = new DefaultChannelGroup(ImmediateEventExecutor.INSTANCE);// 1
	private final EventLoopGroup group = new NioEventLoopGroup();
	private Channel channel;

	public ChannelFuture start(InetSocketAddress address) {
		ServerBootstrap bootstrap = new ServerBootstrap(); // 2
		bootstrap.group(group).channel(NioServerSocketChannel.class).childHandler(createInitializer(channelGroup));
		ChannelFuture future = bootstrap.bind(address);
		future.syncUninterruptibly();
		channel = future.channel();
		return future;
	}

	protected ChannelInitializer<Channel> createInitializer(ChannelGroup group) { // 3
		return new ChatServerInitializer(group);
	}

	public void destroy() { // 4
		if (channel != null) {
			channel.close();
		}
		channelGroup.close();
		group.shutdownGracefully();
	}

	public static void main(String[] args) throws Exception {
		if (args.length != 1) {
			// System.err.println("Please give port as argument");
			// System.exit(1);
			args = new String[1];
			args[0] = "20080";
		}
		int port = Integer.parseInt(args[0]);

		final ChatServer endpoint = new ChatServer();
		ChannelFuture future = endpoint.start(new InetSocketAddress(port));

		System.out.println(ChatServer.class.getName() + " started and listen on " + future.channel().localAddress());
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				endpoint.destroy();
			}
		});
		future.channel().closeFuture().syncUninterruptibly();
	}
}
