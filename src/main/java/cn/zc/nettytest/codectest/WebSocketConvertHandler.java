package cn.zc.nettytest.codectest;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.ContinuationWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

/**
 * 
 * @author zero
 *
 *
 *         1.编码 WebSocketFrame 消息转为 WebSocketFrame 消息 
 *         2.检测 WebSocketFrame 的FrameType 类型，并且创建一个新的响应的 FrameType 类型的 WebSocketFrame
 *         3.通过 instanceof来检测正确的 FrameType 
 *         4.自定义消息类型 WebSocketFrame 
 *         5.枚举类明确了 WebSocketFrame 的类型
 */
public class WebSocketConvertHandler extends
		MessageToMessageCodec<io.netty.handler.codec.http.websocketx.WebSocketFrame, WebSocketConvertHandler.WebSocketFrame> { // 1

	public static final WebSocketConvertHandler INSTANCE = new WebSocketConvertHandler();

	@Override
	protected void encode(ChannelHandlerContext ctx, WebSocketFrame msg, List<Object> out) throws Exception {
		ByteBuf payload = msg.getData().duplicate().retain();
		switch (msg.getType()) { // 2
		case BINARY:
			out.add(new BinaryWebSocketFrame(payload));
			break;
		case TEXT:
			out.add(new TextWebSocketFrame(payload));
			break;
		case CLOSE:
			out.add(new CloseWebSocketFrame(true, 0, payload));
			break;
		case CONTINUATION:
			out.add(new ContinuationWebSocketFrame(payload));
			break;
		case PONG:
			out.add(new PongWebSocketFrame(payload));
			break;
		case PING:
			out.add(new PingWebSocketFrame(payload));
			break;
		default:
			throw new IllegalStateException("Unsupported websocket msg " + msg);
		}
	}

	protected void decode(ChannelHandlerContext ctx, io.netty.handler.codec.http.websocketx.WebSocketFrame msg,
			List<Object> out) throws Exception {
		if (msg instanceof BinaryWebSocketFrame) { // 3
			out.add(new WebSocketFrame(WebSocketFrame.FrameType.BINARY, msg.content().copy()));
		} else if (msg instanceof CloseWebSocketFrame) {
			out.add(new WebSocketFrame(WebSocketFrame.FrameType.CLOSE, msg.content().copy()));
		} else if (msg instanceof PingWebSocketFrame) {
			out.add(new WebSocketFrame(WebSocketFrame.FrameType.PING, msg.content().copy()));
		} else if (msg instanceof PongWebSocketFrame) {
			out.add(new WebSocketFrame(WebSocketFrame.FrameType.PONG, msg.content().copy()));
		} else if (msg instanceof TextWebSocketFrame) {
			out.add(new WebSocketFrame(WebSocketFrame.FrameType.TEXT, msg.content().copy()));
		} else if (msg instanceof ContinuationWebSocketFrame) {
			out.add(new WebSocketFrame(WebSocketFrame.FrameType.CONTINUATION, msg.content().copy()));
		} else {
			throw new IllegalStateException("Unsupported websocket msg " + msg);
		}
	}

	public static final class WebSocketFrame { // 4
		public enum FrameType { // 5
			BINARY, CLOSE, PING, PONG, TEXT, CONTINUATION
		}

		private final FrameType type;
		private final ByteBuf data;

		public WebSocketFrame(FrameType type, ByteBuf data) {
			this.type = type;
			this.data = data;
		}

		public FrameType getType() {
			return type;
		}

		public ByteBuf getData() {
			return data;
		}
	}

}