package cn.zc.nettytest.fixlengthtest;

import org.junit.Assert;
import org.junit.Test;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;

/**
 * 1.测试增加 @Test 注解 
 * 2.新建 ByteBuf 
 * 3.并用字节填充它 新增 EmbeddedChannel 并添加FixedLengthFrameDecoder 
 * 4.用于测试 写数据到 EmbeddedChannel 
 * 5.标记 channel 已经完成 
 * 6.读产生的消息并且校验
 */
public class FixedLengthFrameDecoderTest {
	@Test // 1
	public void testFramesDecoded() {
		ByteBuf buf = Unpooled.buffer(); // 2
		for (int i = 0; i < 9; i++) {
			buf.writeByte(i);
		}
		ByteBuf input = buf.duplicate();

		EmbeddedChannel channel = new EmbeddedChannel(new FixedLengthFrameDecoder(3)); // 3
		Assert.assertFalse(channel.writeInbound(input.readBytes(2))); // 4
		Assert.assertTrue(channel.writeInbound(input.readBytes(7)));

		Assert.assertTrue(channel.finish()); // 5
		ByteBuf read = (ByteBuf) channel.readInbound();
		Assert.assertEquals(buf.readSlice(3), read);
		read.release();

		read = (ByteBuf) channel.readInbound();
		Assert.assertEquals(buf.readSlice(3), read);
		read.release();

		read = (ByteBuf) channel.readInbound();
		Assert.assertEquals(buf.readSlice(3), read);
		read.release();

		Assert.assertNull(channel.readInbound());
		buf.release();
	}

	@Test
	public void testFramesDecoded2() {
		ByteBuf buf = Unpooled.buffer();
		for (int i = 0; i < 9; i++) {
			buf.writeByte(i);
		}
		ByteBuf input = buf.duplicate();

		EmbeddedChannel channel = new EmbeddedChannel(new FixedLengthFrameDecoder(3));
		Assert.assertFalse(channel.writeInbound(input.readBytes(2)));
		Assert.assertTrue(channel.writeInbound(input.readBytes(7)));

		Assert.assertTrue(channel.finish());
		ByteBuf read = (ByteBuf) channel.readInbound();
		Assert.assertEquals(buf.readSlice(3), read);
		read.release();

		read = (ByteBuf) channel.readInbound();
		Assert.assertEquals(buf.readSlice(3), read);
		read.release();

		read = (ByteBuf) channel.readInbound();
		Assert.assertEquals(buf.readSlice(3), read);
		read.release();

		Assert.assertNull(channel.readInbound());
		buf.release();
	}
}
