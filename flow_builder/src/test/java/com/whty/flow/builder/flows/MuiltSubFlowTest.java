package com.whty.flow.builder.flows;

import org.junit.Test;

import com.whty.flow.EasyFlow;
import com.whty.flow.builder.xml.XMLFlowBuilder;
import com.whty.flow.container.FlowContainer;
import com.whty.flow.impl.context.FlowMapContext;

import junit.framework.TestCase;

/**
 * 多套子流程测试
 *
 */
public class MuiltSubFlowTest extends TestCase{

	@Test
	public void testSubFlowBuilder() {
		XMLFlowBuilder builder = new XMLFlowBuilder();
		
		EasyFlow<FlowMapContext> subflow1 = builder
				.buildFlow("template//muiltsubflow//appDownloadSubflow.xml");
		
		FlowContainer.setFlow(subflow1);
		
		EasyFlow<FlowMapContext> subflow2 = builder
				.buildFlow("template//muiltsubflow//appDownloadSubflow2.xml");
		
		FlowContainer.setFlow(subflow2);
		
		EasyFlow<FlowMapContext> parentflow = builder
				.buildFlow("template//muiltsubflow//appDownloadParentFlow.xml");
		
		FlowContainer.setFlow(parentflow);
		
		FlowMapContext ctx = new FlowMapContext();
		parentflow.instance().trace().start(ctx);
		assertEquals("GetAppBaseInfoHandler", ctx.getParam("data"));
		
		assertTrue(ctx.isTerminated());
		FlowContainer.clear();
	}
	
}
