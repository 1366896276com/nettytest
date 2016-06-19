package cn.zc.nettytest.httptest;

import javax.net.ssl.SSLEngine;

import cn.zc.nettytest.util.SslContext;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.ssl.SslHandler;

/**
 * 
 * @author zero
 *
 *
 *         1.添加 SslHandler 到 pipeline 来启用 HTTPS 
 *         2.client: 添加 HttpClientCodec 
 *         3.server: 添加 HttpServerCodec ，如果是 server 模式的话
 */
public class HttpsCodecInitializer extends ChannelInitializer<Channel> {

	private final SslContext context;
	private final boolean client;

	public HttpsCodecInitializer(SslContext context, boolean client) {
		this.context = context;
		this.client = client;
	}

	@Override
	protected void initChannel(Channel ch) throws Exception {
		ChannelPipeline pipeline = ch.pipeline();
		
		SSLEngine engine = context.newEngine(ch.alloc());
		pipeline.addFirst("ssl", new SslHandler(engine)); // 1

		if (client) {
			pipeline.addLast("codec", new HttpClientCodec()); // 2
		} else {
			pipeline.addLast("codec", new HttpServerCodec()); // 3
		}
	}
}