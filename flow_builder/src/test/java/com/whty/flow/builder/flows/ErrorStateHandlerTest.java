package com.whty.flow.builder.flows;

import com.whty.flow.EasyFlow;
import com.whty.flow.builder.xml.XMLFlowBuilder;
import com.whty.flow.container.FlowContainer;
import com.whty.flow.impl.context.FlowMapContext;

import junit.framework.TestCase;

/**
 * 异常情况测试
 */
public class ErrorStateHandlerTest extends TestCase {

	/**
	 * 异常流程
	 */
	public void testErrorHandler() {
		XMLFlowBuilder builder = new XMLFlowBuilder();

		EasyFlow<FlowMapContext> flow = builder
				.buildFlow("template//error//errorFlow.xml");

		FlowContainer.setFlow(flow);
		FlowMapContext ctx = new FlowMapContext();
		flow.instance().trace().start(ctx);
		assertNotNull(ctx.getParam("error"));
		FlowContainer.clear();
	}
	
	/**
	 * 测试自定义异常
	 */
	public void testErrorHandler2() {
		XMLFlowBuilder builder = new XMLFlowBuilder();

		EasyFlow<FlowMapContext> flow = builder
				.buildFlow("template//error//errorFlow2.xml");

		FlowContainer.setFlow(flow);
		FlowMapContext ctx = new FlowMapContext();
		flow.instance().trace().start(ctx);
		assertNotNull(ctx.getParam("error"));
		assertNotNull(ctx.getParam("error2"));
		FlowContainer.clear();
	}
}
