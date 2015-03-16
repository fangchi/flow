package com.whty.flow.spring;

import java.io.File;

import junit.framework.TestCase;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.whty.flow.container.FlowContainer;

public class FlowSpringTest extends TestCase{

	@SuppressWarnings("static-access")
	@Test
	public void testIntegrateSpring(){
		ApplicationContext applicationContext = new FileSystemXmlApplicationContext("file:"
				+ new File("").getAbsolutePath()
				+ "//src//test//resources//spring//spring-init.xml");
		FlowContainer container = applicationContext.getBean(FlowContainer.class);
		assertNotNull(container);
		assertNotNull(container.getFlow("tath.005.001.01"));
	}
}
