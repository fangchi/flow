package com.whty.flow.builder.flows;

import junit.framework.TestCase;

import org.junit.Test;

import com.whty.flow.EasyFlow;
import com.whty.flow.builder.xml.XMLFlowBuilder;
import com.whty.flow.container.FlowContainer;
import com.whty.flow.impl.context.FlowMapContext;

public class FlowParamBuilderTest extends TestCase{

	/**
	 * 流程变量测试
	 */
	@Test
	public void testSimpleBuilder() {
		XMLFlowBuilder builder = new XMLFlowBuilder();
		EasyFlow<FlowMapContext> flow = builder
				.buildFlow("template//param//appDownload.xml");
		assertEquals("应用下载模板", flow.getName());
		FlowMapContext ctx = new FlowMapContext();
		flow.instance().trace().start(ctx);
		assertTrue(ctx.isTerminated());
		assertEquals("GetAppBaseInfoHandler", ctx.getParam("data"));
		FlowContainer.clear();
	}
}
