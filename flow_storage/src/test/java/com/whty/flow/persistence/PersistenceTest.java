package com.whty.flow.persistence;

import junit.framework.TestCase;

import org.junit.Test;

import com.google.gson.Gson;
import com.whty.flow.EasyFlow;
import com.whty.flow.FlowInstance;
import com.whty.flow.builder.xml.XMLFlowBuilder;
import com.whty.flow.container.FlowContainer;
import com.whty.flow.impl.context.FlowMapContext;
import com.whty.flow.pojo.TestBean;
import com.whty.flow.pojo.TestBeanTwo;

public class PersistenceTest extends TestCase{

	@SuppressWarnings("unchecked")
	@Test
	public void testSingleFlowPersistence() throws InterruptedException {
		XMLFlowBuilder builder = new XMLFlowBuilder();
		EasyFlow<FlowMapContext> flow = builder
				.buildFlow("template//basic//appDownloadSuspanded.xml");
		assertEquals("应用下载模板", flow.getName());
		FlowContainer.setFlow(flow);
		FlowMapContext ctx = new FlowMapContext();
		FlowInstance<FlowMapContext> instance = flow.instance().trace();
		ctx.addParam("key1", "val1");
		instance.start(ctx);
		assertTrue(!ctx.isTerminated());
		
		StorageUtil.save(ctx.getId(), ctx, StorageMode.RAM);
		Thread.sleep(1000);
		FlowMapContext ctx2 = new FlowMapContext();
		ctx2 = StorageUtil.get(ctx.getId(), ctx2,StorageMode.RAM);
		ctx2.getInstanceToContinue().toContinue(ctx2);
		assertEquals("GetAppBaseInfoHandler", ctx2.getParam("data"));
		assertTrue(ctx2.isTerminated());
		FlowContainer.clear();
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testSingleFlowWithComplexDataPersistence() throws InterruptedException {
		TestBean bean = new TestBean("ipingjing", 32);
		bean.getProC().add(new TestBeanTwo("testA"));
		bean.getProC().add(new TestBeanTwo("testB"));
		
		XMLFlowBuilder builder = new XMLFlowBuilder();
		EasyFlow<FlowMapContext> flow = builder
				.buildFlow("template//basic//appDownloadSuspanded.xml");
		assertEquals("应用下载模板", flow.getName());
		FlowContainer.setFlow(flow);
		FlowMapContext ctx = new FlowMapContext();
		FlowInstance<FlowMapContext> instance = flow.instance().trace();
		ctx.addParam("bean", new Gson().toJson(bean));
		instance.start(ctx);
		assertTrue(!ctx.isTerminated());
		
		StorageUtil.save(ctx.getId(), ctx, StorageMode.RAM);
		Thread.sleep(1000);
		FlowMapContext ctx2 = new FlowMapContext();
		ctx2 = StorageUtil.get(ctx.getId(), ctx2,StorageMode.RAM);
		ctx2.getInstanceToContinue().toContinue(ctx2);
		assertEquals("GetAppBaseInfoHandler", ctx2.getParam("data"));
		assertNotNull(ctx2.getParam("bean"));
		String bean2Str = (String)ctx2.getParam("bean");
		TestBean bean2 = new Gson().fromJson(bean2Str, TestBean.class);
		assertEquals(new Integer(32), bean2.getProB());
		assertEquals("testA", bean2.getProC().get(0).getProA());
		assertTrue(ctx2.isTerminated());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testSubFlowPersistence() throws InterruptedException {
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
		
		StorageUtil.save(ctx.getId(), ctx, StorageMode.RAM);
		Thread.sleep(1000);
		FlowMapContext ctx2 = new FlowMapContext();
		ctx2 = StorageUtil.get(ctx.getId(), ctx2,StorageMode.RAM);
		ctx2.getInstanceToContinue().toContinue(ctx2);
		assertEquals("getAppBaseInfo", ctx2.getState().getId());
		assertTrue(!ctx.isTerminated());
		
		StorageUtil.save(ctx2.getId(), ctx2, StorageMode.RAM);
		Thread.sleep(1000);
		FlowMapContext ctx3 = new FlowMapContext();
		ctx3 = StorageUtil.get(ctx2.getId(), ctx3,StorageMode.RAM);
		ctx3.getFlowInstance().toContinue(ctx3);
		
		assertTrue(ctx3.isTerminated());
		FlowContainer.clear();
	}
}
