package cn.zc.nettytest.httptest;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpContentDecompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;

/**
 * 
 * @author zero
 *
 *
 *         1.client: 添加 HttpClientCodec 
 *         2.server: 添加 HttpServerCodec 作为我们是server模式时 
 *         3.添加 HttpObjectAggregator 到 ChannelPipeline, 使用最大消息值是 512kb
 */
public class HttpAggregatorInitializer extends ChannelInitializer<Channel> {

	private final boolean client;

	public HttpAggregatorInitializer(boolean client) {
		this.client = client;
	}

	@Override
	protected void initChannel(Channel ch) throws Exception {
		ChannelPipeline pipeline = ch.pipeline();
		if (client) {
			pipeline.addLast("codec", new HttpClientCodec()); // 1
			// 解压功能
			pipeline.addLast("decompressor", new HttpContentDecompressor());
		} else {
			pipeline.addLast("codec", new HttpServerCodec()); // 2
			// 压缩功能
			pipeline.addLast("compressor", new HttpContentCompressor());
		}
		pipeline.addLast("aggegator", new HttpObjectAggregator(512 * 1024)); // 3

	}

	/**
	 * 注意，Java 6或者更早版本，如果要压缩数据，需要添加 jzlib 到 classpath
	 * <dependency> <groupId>com.jcraft</groupId> <artifactId>jzlib</artifactId>
	 * <version>1.1.3</version> </dependency>
	 */
}