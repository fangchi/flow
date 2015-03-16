package com.whty.flow;

import java.util.HashMap;

import junit.framework.TestCase;

import org.junit.Test;

import com.whty.flow.call.ContextHandler;

/**
 * 并发测试
 * @author Fangchi
 *
 */
public class SynchronizationTest extends TestCase{

	private static HashMap<String, Object> params = new HashMap<String, Object>();

	@Test
	public void testSync() {
		FlowBuilder<FlowMapTestContext> flowBuilder = new FlowBuilder<FlowMapTestContext>();

		EasyFlow<FlowMapTestContext> flow = flowBuilder.from("flow_1", "流程名称",
				"1", "start_state", params);

		flowBuilder.transit(
				flow,
				flowBuilder
						.on(new Event("1"), flow)
						.to("2", "state_2", params)
						.transit(
								flowBuilder.on(new Event("3"), flow).to("1",
										"start_state", params)),
				flowBuilder
						.on(new Event("2"), flow)
						.to("3", "state_3", true, params)
						.transit(
								flowBuilder.on(new Event("2"), flow).finish(
										"4", "state_4", params),
								flowBuilder.on(new Event("1"), flow).finish(
										"5", "state_5", params)));

		flow.whenEnter(new State("1", "start_state", flow, params),
				new ContextHandler<FlowMapTestContext>() {
					@Override
					public void call(FlowMapTestContext context)
							throws Exception {
						System.out.println(context);
						context.getParamMap().put("key",
								"val" + Thread.currentThread().getId());
						context.trigger(new Event("2"));
					}
				}).whenEnter(new State("3", "state_3", flow, params),
				new ContextHandler<FlowMapTestContext>() {
					@Override
					public void call(FlowMapTestContext context)
							throws Exception {
						System.out.println(context);

						context.trigger(new Event("2"));
					}
				});

		for (int i = 0; i < 5; i++) {
			new TestThread(flow).start();
		}
	}
}
