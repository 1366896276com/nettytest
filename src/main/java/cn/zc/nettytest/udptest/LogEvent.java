package cn.zc.nettytest.udptest;

import java.net.InetSocketAddress;

/**
 * 
 * @author zero
 *
 *         1.构造器用于出站消息 
 *         2.构造器用于入站消息 
 *         3.返回发送 LogEvent 的 InetSocketAddress 的资源 
 *         4.返回用于发送LogEvent 的日志文件的名称 
 *         5.返回消息的内容 
 *         6.返回 LogEvent 接收到的时间
 */
public final class LogEvent {
	public static final byte SEPARATOR = (byte) ':';

	private final InetSocketAddress source;
	private final String logfile;
	private final String msg;
	private final long received;

	public LogEvent(String logfile, String msg) { // 1
		this(null, -1, logfile, msg);
	}

	public LogEvent(InetSocketAddress source, long received, String logfile, String msg) { // 2
		this.source = source;
		this.logfile = logfile;
		this.msg = msg;
		this.received = received;
	}

	public InetSocketAddress getSource() { // 3
		return source;
	}

	public String getLogfile() { // 4
		return logfile;
	}

	public String getMsg() { // 5
		return msg;
	}

	public long getReceivedTimestamp() { // 6
		return received;
	}
}