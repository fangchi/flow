package com.whty.flow;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;

import org.junit.Test;

import com.whty.flow.call.ContextHandler;

/**
 * 流程参数构建
 */
public class ParamTest {

	@Test
	public void testParamContext() {
		FlowBuilder<FlowMapTestContext> flowBuilder = new FlowBuilder<FlowMapTestContext>();

		HashMap<String, Object> params1 = new HashMap<String, Object>();
		params1.put("params1", "val1");
		HashMap<String, Object> params2 = new HashMap<String, Object>();
		params2.put("params2", "val2");
		HashMap<String, Object> params3 = new HashMap<String, Object>();
		params3.put("params3", "val3");
		HashMap<String, Object> params4 = new HashMap<String, Object>();
		params4.put("params4", "val4");
		HashMap<String, Object> params5 = new HashMap<String, Object>();
		params5.put("params5", "val5");

		EasyFlow<FlowMapTestContext> flow = flowBuilder.from("flow_1",
				"testFlow", "1", "start_state", params1);

		flowBuilder.transit(
				flow,
				flowBuilder
						.on(new Event("1"), flow)
						.to("2", "state_2", params2)
						.transit(
								flowBuilder.on(new Event("3"), flow).to("1",
										"start_state", params1)),
				flowBuilder
						.on(new Event("2"), flow)
						.to("3", "state_3", true, params3)
						.transit(
								flowBuilder.on(new Event("2"), flow).finish(
										"4", "state_4", params4),
								flowBuilder.on(new Event("1"), flow).finish(
										"5", "state_5", params5)));

		flow.whenEnter(new State("1", "start_state", flow, params1),
				new ContextHandler<FlowMapTestContext>() {
					@Override
					public void call(FlowMapTestContext context)
							throws Exception {
						assertEquals("val1",
								context.getState().getParam("params1"));
						context.trigger(new Event("2"));
					}
				}).whenEnter(new State("3", "state_3", flow, params3),
				new ContextHandler<FlowMapTestContext>() {
					@Override
					public void call(FlowMapTestContext context)
							throws Exception {
						assertEquals("val3",
								context.getState().getParam("params3"));
						context.trigger(new Event("2"));
					}
				});
		FlowMapTestContext ctx = new FlowMapTestContext();
		flow.instance().trace().start(ctx);
	}
}
