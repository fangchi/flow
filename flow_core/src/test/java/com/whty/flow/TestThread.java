package com.whty.flow;

public class TestThread extends Thread {

	private EasyFlow<FlowMapTestContext> flow ;
	
	public TestThread(EasyFlow<FlowMapTestContext> flow){
		this.flow  = flow;
	}
	
	@Override
	
	public void run() {
//		FlowMapContext ctx = new FlowMapContext();
//		flow.instance().trace().start(ctx);
//		assertEquals("val", ctx.getParamMap().get("key"));
		FlowMapTestContext ctx = new FlowMapTestContext();
		flow.instance().trace().start(ctx);
	}
	
}