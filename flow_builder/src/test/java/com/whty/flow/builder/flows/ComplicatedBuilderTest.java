package com.whty.flow.builder.flows;

import org.junit.Test;

import com.whty.flow.EasyFlow;
import com.whty.flow.FlowInstance;
import com.whty.flow.builder.xml.XMLFlowBuilder;
import com.whty.flow.container.FlowContainer;
import com.whty.flow.impl.context.FlowMapContext;

import junit.framework.TestCase;

/**
 * 复杂流程实例构建情况
 */
public class ComplicatedBuilderTest extends TestCase {

	/**
	 * 测试子流程
	 */
	@Test
	public void testSubFlowBuilder() {
		XMLFlowBuilder builder = new XMLFlowBuilder();
		
		EasyFlow<FlowMapContext> subflow = builder
				.buildFlow("template//comp//appDownloadSubflow.xml");
		
		FlowContainer.setFlow(subflow);
		
		EasyFlow<FlowMapContext> parentflow = builder
				.buildFlow("template//comp//appDownloadParentFlow.xml");
		
		FlowContainer.setFlow(parentflow);
		
		FlowMapContext ctx = new FlowMapContext();
		parentflow.instance().trace().start(ctx);
		assertEquals("GetAppBaseInfoHandler",  ctx.getParam("data"));
		FlowContainer.clear();
	}
	
	/**
	 * 测试子流程挂起情况
	 */
	@Test
	public void testSubFlowBuilderWithSuspand() {
		XMLFlowBuilder builder = new XMLFlowBuilder();
		
		EasyFlow<FlowMapContext> subflow = builder
				.buildFlow("template//comp//appDownloadSubflowWithSuspand.xml");
		
		FlowContainer.setFlow(subflow);
		
		EasyFlow<FlowMapContext> parentflow = builder
				.buildFlow("template//comp//appDownloadParentflowWithSuspand.xml");
		
		FlowContainer.setFlow(parentflow);
		
		FlowMapContext ctx = new FlowMapContext();
		FlowInstance<FlowMapContext> flowInstance  = parentflow.instance().trace();
		flowInstance.start(ctx);
		assertTrue(!ctx.isTerminated());
		assertEquals("isValidSd", ctx.getState().getId());
		assertEquals("tath.005.001.05", ctx.getFlowInstance().getFlow().getId());
		flowInstance.toContinue(ctx);
		assertEquals("getAppBaseInfo", ctx.getState().getId());
		assertTrue(!ctx.isTerminated());
		flowInstance.toContinue(ctx);
		assertTrue(ctx.isTerminated());
		FlowContainer.clear();
	}
	
	/**
	 * 子路程挂起情况
	 * @throws InterruptedException
	 */
	@Test
	public void testSubFlowBuilderWithSuspand2() throws InterruptedException {
		XMLFlowBuilder builder = new XMLFlowBuilder();
		
		EasyFlow<FlowMapContext> subflow = builder
				.buildFlow("template//comp//appDownloadSubflowWithSuspand.xml");
		
		FlowContainer.setFlow(subflow);
		
		EasyFlow<FlowMapContext> parentflow = builder
				.buildFlow("template//comp//appDownloadParentflowWithSuspand2.xml");
		
		FlowContainer.setFlow(parentflow);
		
		FlowMapContext ctx = new FlowMapContext();
		FlowInstance<FlowMapContext> flowInstance  = parentflow.instance().trace();
		flowInstance.start(ctx);
		assertTrue(!ctx.isTerminated());
		assertEquals("isValidSd", ctx.getState().getId());
		assertEquals("tath.005.001.05", ctx.getFlowInstance().getFlow().getId());
		flowInstance.toContinue(ctx);
		assertEquals("GetAppBaseInfoHandler", ctx.getParam("data"));
		assertTrue(!ctx.isTerminated());
		assertEquals("isValidSd", ctx.getState().getId());
		assertEquals("tath.005.001.07", ctx.getFlowInstance().getFlow().getId());
		flowInstance.toContinue(ctx);
		assertEquals("tath.005.001.07", ctx.getFlowInstance().getFlow().getId());
		assertTrue(ctx.isTerminated());
		FlowContainer.clear();
	}
}
