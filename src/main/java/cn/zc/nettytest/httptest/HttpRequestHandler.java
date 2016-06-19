package cn.zc.nettytest.httptest;

import java.io.File;
import java.io.RandomAccessFile;
import java.net.URISyntaxException;
import java.net.URL;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.DefaultFileRegion;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.stream.ChunkedNioFile;

/**
 * 
 * 
 * @author zero
 *
 *         1.扩展 SimpleChannelInboundHandler 用于处理 FullHttpRequest信息
 * 
 *         2.如果请求是一次升级了的 WebSocket 请求，则递增引用计数器（retain）并且将它传递给在 ChannelPipeline
 *         中的下个 ChannelInboundHandler
 * 
 *         3.处理符合 HTTP 1.1的 "100 Continue" 请求
 * 
 *         4.读取 index.html
 * 
 *         5.判断 keepalive 是否在请求头里面
 * 
 *         6.写 HttpResponse 到客户端
 * 
 *         7.写 index.html 到客户端，根据 ChannelPipeline 中是否有 SslHandler 来决定使用
 *         DefaultFileRegion 还是 ChunkedNioFile
 * 
 *         8.写并刷新 LastHttpContent 到客户端，标记响应完成
 * 
 *         9.如果 请求头中不包含 keepalive，当写完成时，关闭 Channel
 * 
 *         HttpRequestHandler 做了下面几件事，
 * 
 *         如果该 HTTP 请求被发送到URI “/ws”，则调用 FullHttpRequest 上的 retain()，并通过调用
 *         fireChannelRead(msg) 转发到下一个 ChannelInboundHandler。retain() 的调用是必要的，因为
 *         channelRead() 完成后，它会调用 FullHttpRequest 上的 release() 来释放其资源。
 *         （请参考我们先前在第6章中关于 SimpleChannelInboundHandler 的讨论） 如果客户端发送的 HTTP 1.1
 *         头是“Expect: 100-continue” ，则发送“100 Continue”的响应。 在 头被设置后，写一个
 *         HttpResponse 返回给客户端。注意，这不是 FullHttpResponse，这只是响应的第一部分。另外，这里我们也不使用
 *         writeAndFlush()， 这个是在留在最后完成。 如果传输过程既没有要求加密也没有要求压缩，那么把 index.html
 *         的内容存储在一个 DefaultFileRegion 里就可以达到最好的效率。这将利用零拷贝来执行传输。出于这个原因，我们要检查
 *         ChannelPipeline 中是否有一个 SslHandler。如果是的话，我们就使用 ChunkedNioFile。 写
 *         LastHttpContent 来标记响应的结束，并终止它 如果不要求 keepalive ，添加
 *         ChannelFutureListener 到 ChannelFuture 对象的最后写入，并关闭连接。注意，这里我们调用
 *         writeAndFlush() 来刷新所有以前写的信息。
 */
public class HttpRequestHandler extends SimpleChannelInboundHandler<FullHttpRequest> { // 1
	private final String wsUri;
	private static final File INDEX;

	static {
		URL location = HttpRequestHandler.class.getProtectionDomain().getCodeSource().getLocation();
		try {
			String path = location.toURI() + "index.html";
			path = !path.contains("file:") ? path : path.substring(5);
			INDEX = new File(path);
		} catch (URISyntaxException e) {
			throw new IllegalStateException("Unable to locate index.html", e);
		}
	}

	public HttpRequestHandler(String wsUri) {
		this.wsUri = wsUri;
	}

	@Override
	public void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
		//这里是根据url来判断用什么协议处理
		if (wsUri.equalsIgnoreCase(request.getUri())) {
			//处理websocket
			ctx.fireChannelRead(request.retain()); // 2
		} else {
			//处理http
			if (HttpHeaders.is100ContinueExpected(request)) {
				send100Continue(ctx); // 3
			}

			RandomAccessFile file = new RandomAccessFile(INDEX, "r");// 4

			HttpResponse response = new DefaultHttpResponse(request.getProtocolVersion(), HttpResponseStatus.OK);
			response.headers().set(HttpHeaders.Names.CONTENT_TYPE, "text/html; charset=UTF-8");

			boolean keepAlive = HttpHeaders.isKeepAlive(request);

			if (keepAlive) { // 5
				response.headers().set(HttpHeaders.Names.CONTENT_LENGTH, file.length());
				response.headers().set(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
			}
			ctx.write(response); // 6

			if (ctx.pipeline().get(SslHandler.class) == null) { // 7
				ctx.write(new DefaultFileRegion(file.getChannel(), 0, file.length()));
			} else {
				ctx.write(new ChunkedNioFile(file.getChannel()));
			}
			ChannelFuture future = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT); // 8
			if (!keepAlive) {
				future.addListener(ChannelFutureListener.CLOSE); // 9
			}
		}
	}

	private static void send100Continue(ChannelHandlerContext ctx) {
		FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.CONTINUE);
		ctx.writeAndFlush(response);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		ctx.close();
	}
}
