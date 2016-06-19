package cn.zc.nettytest.util;

import java.net.InetSocketAddress;

import cn.zc.nettytest.echotest.EchoServer;
import cn.zc.nettytest.echotest.EchoServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class ServerUtil {
	public static void start(int port,ChannelInitializer<Channel> init) throws Exception {
		NioEventLoopGroup group = new NioEventLoopGroup(); // 3
		try {
			ServerBootstrap b = new ServerBootstrap();
			b.group(group) // 4
					.channel(NioServerSocketChannel.class) // 5
					.localAddress(new InetSocketAddress(port)) // 6
					.childHandler(init);

			ChannelFuture f = b.bind().sync(); // 8
			System.out.println(EchoServer.class.getName() + " started and listen on " + f.channel().localAddress());
			
			//等待程序中退出
			f.channel().closeFuture().sync(); // 9
		} finally {
			//优雅关闭
			group.shutdownGracefully().sync(); // 10
		}
	}
}
