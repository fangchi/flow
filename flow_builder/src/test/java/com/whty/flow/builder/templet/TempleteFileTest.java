package com.whty.flow.builder.templet;

import junit.framework.TestCase;

import org.junit.Test;

import com.whty.flow.builder.xml.XMLFlowBuilder;
import com.whty.flow.container.FlowContainer;

/**
 * 总体定义文件测试
 */
public class TempleteFileTest extends TestCase{

	@Test
	public void testSimpleBuilder() {
		XMLFlowBuilder builder = new XMLFlowBuilder();
		builder.build("build/build.xml");
		assertNotNull(FlowContainer.getFlow("tath.005.001.01"));
		FlowContainer.clear();
	}
}
