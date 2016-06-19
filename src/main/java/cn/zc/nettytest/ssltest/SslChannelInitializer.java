package cn.zc.nettytest.ssltest;

import javax.net.ssl.*;

import cn.zc.nettytest.util.SslContext;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.ssl.SslHandler;

/**
 * 1.使用构造函数来传递 SSLContext 用于使用(startTls 是否启用) 
 * 2.从 SslContext 获得一个新的 SslEngine。给每个 SslHandler 实例使用一个新的 SslEngine 
 * 3.设置 SslEngine 是 client 或者是 server 模式 
 * 4.添加SslHandler 到 pipeline 作为第一个处理器
 * 
 * 
 */
public class SslChannelInitializer extends ChannelInitializer<Channel> {

	private final SslContext context;
	private final boolean startTls;
	private final boolean client;

	public SslChannelInitializer(SslContext context, boolean client, boolean startTls) { // 1
		this.context = context;
		this.startTls = startTls;
		this.client=client;
	}

	@Override
	protected void initChannel(Channel ch) throws Exception {
		SSLEngine engine = context.newEngine(ch.alloc()); // 2
		engine.setUseClientMode(client); // 3
		ch.pipeline().addFirst("ssl", new SslHandler(engine, startTls)); // 4
	}
}