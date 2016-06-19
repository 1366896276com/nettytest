package cn.zc.nettytest.udptest;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.util.CharsetUtil;

/**
 * 
 * 
 * @author zero
 *
 *
 *         1.获取 DatagramPacket 中数据的引用 
 *         2.获取 SEPARATOR 的索引 
 *         3.从数据中读取文件名 
 *         4.读取数据中的日志消息 
 *         5.构造新的 LogEvent对象并将其添加到列表中
 */
public class LogEventDecoder extends MessageToMessageDecoder<DatagramPacket> {
	@Override
	protected void decode(ChannelHandlerContext ctx, DatagramPacket datagramPacket, List<Object> out) throws Exception {
		ByteBuf data = datagramPacket.content(); // 1
		int i = data.indexOf(0, data.readableBytes(), LogEvent.SEPARATOR); // 2
		String filename = data.slice(0, i).toString(CharsetUtil.UTF_8); // 3
		String logMsg = data.slice(i + 1, data.readableBytes()).toString(CharsetUtil.UTF_8); // 4

		LogEvent event = new LogEvent(datagramPacket.recipient(), System.currentTimeMillis(), filename, logMsg); // 5
		out.add(event);
	}
}