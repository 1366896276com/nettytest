package cn.zc.nettytest.httptest;

import org.junit.Test;

import cn.zc.nettytest.util.ServerUtil;

public class TestInitializer {

	@Test 
	public void testIdle() throws Exception{
		ServerUtil.start(20080,	new IdleStateHandlerInitializer());
	}
	
	@Test 
	public void testHttpClient() throws Exception{
		ServerUtil.start(20080,	new HttpAggregatorInitializer(true));
	}
	
	@Test 
	public void testHttpServer() throws Exception{
		ServerUtil.start(20080,	new HttpAggregatorInitializer(false));
	}
}
