package com.whty.flow.builder.flows;

import org.junit.Test;

import com.whty.flow.EasyFlow;
import com.whty.flow.FlowInstance;
import com.whty.flow.builder.xml.XMLFlowBuilder;
import com.whty.flow.container.FlowContainer;
import com.whty.flow.impl.context.FlowMapContext;

import junit.framework.TestCase;

/**
 * 基础构建测试用例
 */
public class BasicFlowBuilderTest extends TestCase{

	/**
	 * 通用流程测试
	 */
	@Test
	public void testSimpleBuilder() {
		XMLFlowBuilder builder = new XMLFlowBuilder();
		EasyFlow<FlowMapContext> flow = builder
				.buildFlow("template//basic//appDownload.xml");
		assertEquals("应用下载模板", flow.getName());
		FlowMapContext ctx = new FlowMapContext();
		flow.instance().trace().start(ctx);
		assertTrue(ctx.isTerminated());
		assertEquals("GetAppBaseInfoHandler", ctx.getParam("data"));
		FlowContainer.clear();
	}
	
	/**
	 * 测试有循环的state的情况
	 */
	@Test
	public void testCircleStateBuilder() {
		XMLFlowBuilder builder = new XMLFlowBuilder();
		EasyFlow<FlowMapContext> flow = builder
				.buildFlow("template//basic//appPersonalize.xml");
		assertEquals("应用个人化模板", flow.getName());
		FlowMapContext ctx = new FlowMapContext();
		FlowInstance<FlowMapContext> instance = flow.instance().trace();
		instance.start(ctx);
		assertTrue(ctx.isTerminated());
		FlowContainer.clear();
	}
	
	/**
	 * 测试挂起
	 */
	@Test
	public void testSimpleWithSuspandedBuilder() {
		XMLFlowBuilder builder = new XMLFlowBuilder();
		EasyFlow<FlowMapContext> flow = builder
				.buildFlow("template//basic//appDownloadSuspanded.xml");
		assertEquals("应用下载模板", flow.getName());
		FlowMapContext ctx = new FlowMapContext();
		FlowInstance<FlowMapContext> instance = flow.instance().trace();
		instance.start(ctx);
		assertTrue(!ctx.isTerminated());
		instance.toContinue(ctx);
		assertEquals("GetAppBaseInfoHandler",  ctx.getParam("data"));
		assertTrue(ctx.isTerminated());
		FlowContainer.clear();
	}
	
	/**
	 * 测试连续挂起情况
	 */
	@Test
	public void testSimpleWithSuspandedTwiceBuilder() {
		XMLFlowBuilder builder = new XMLFlowBuilder();
		EasyFlow<FlowMapContext> flow = builder
				.buildFlow("template//basic//appDownloadSuspandedTwice.xml");
		assertEquals("应用下载模板", flow.getName());
		FlowMapContext ctx = new FlowMapContext();
		FlowInstance<FlowMapContext> instance = flow.instance().trace();
		instance.start(ctx);
		assertTrue(!ctx.isTerminated());
		instance.toContinue(ctx);
		instance.toContinue(ctx);
		assertEquals("GetAppBaseInfoHandler", ctx.getParam("data"));
		assertTrue(ctx.isTerminated());
		FlowContainer.clear();
	}
	
}
