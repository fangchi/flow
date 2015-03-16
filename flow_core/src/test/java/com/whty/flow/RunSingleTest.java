package com.whty.flow;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.List;

import org.junit.Test;

import com.google.common.collect.Lists;
import com.whty.flow.call.ContextHandler;
import com.whty.flow.call.ExecutionErrorHandler;
import com.whty.flow.call.StateHandler;
import com.whty.flow.err.ExecutionError;
import com.whty.flow.err.LogicViolationError;

/**
 * 流程基本测试
 */
public class RunSingleTest {

	// 初始化参数
	private HashMap<String, Object> params = new HashMap<String, Object>();

	/**
	 * 非法事件测试
	 * 
	 * @throws LogicViolationError
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void testInvalidEvent() throws LogicViolationError {
		final Exception[] exception = new Exception[] { null };

		FlowBuilder flowBuilder = new FlowBuilder();

		EasyFlow<StatefulContext> flow = flowBuilder.from("flow_1", "testFlow",
				"1", "start_state", params);

		flowBuilder.transit(
				flow,
				flowBuilder
						.on(new Event("1"), flow)
						.to("2", "state_2", params)
						.transit(
								flowBuilder.on(new Event("2"), flow).finish(
										"3", "state_3", params)));

		// handlers definition
		flow.whenError(new ExecutionErrorHandler() {
			@Override
			public void call(ExecutionError error, StatefulContext context) {
				exception[0] = error;
			}
		})

		.whenEnter(new State("1", "start_state", flow, params),
				new ContextHandler<StatefulContext>() {
					@Override
					public void call(StatefulContext context) throws Exception {
						context.trigger(new Event("2"));
					}
				});

		StatefulContext ctx = new StatefulContext();
		flow.instance().trace().start(ctx);
		// ctx.awaitTermination();

		assertNotNull("Exception must be thrown during flow execution",
				exception[0]);
		assertTrue("Exception type should be ExecutionError",
				exception[0] instanceof ExecutionError);
		assertTrue("Exception cause should be LogicViolationError",
				exception[0].getCause() instanceof LogicViolationError);
	}

	/**
	 * 执行顺序测试
	 * 
	 * @throws LogicViolationError
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void testEventsOrder() throws LogicViolationError {

		FlowBuilder flowBuilder = new FlowBuilder();

		EasyFlow<StatefulContext> flow = flowBuilder.from("flow_1", "testFlow",
				"1", "start_state", params);

		flowBuilder.transit(
				flow,
				flowBuilder
						.on(new Event("1"), flow)
						.to("2", "state_2", params)
						.transit(
								flowBuilder.on(new Event("2"), flow).finish(
										"3", "state_3", params),
								flowBuilder
										.on(new Event("3"), flow)
										.to("4", "state_4", params)
										.transit(
												flowBuilder.on(new Event("4"),
														flow).finish("5",
														"state_5", params),
												flowBuilder.on(new Event("5"),
														flow).to("2",
														"state_2", params))));
		final List<Integer> actualOrder = Lists.newArrayList();

		flow.whenEnter(new State("1", "start_state", flow, params),
				new ContextHandler<StatefulContext>() {
					@Override
					public void call(StatefulContext context)
							throws InterruptedException {
						actualOrder.add(1);
						context.trigger(new Event("1"));
					}
				})

				.whenLeave(new State("1", "start_state", flow, params),
						new ContextHandler<StatefulContext>() {
							@Override
							public void call(StatefulContext context) {
								actualOrder.add(2);
							}
						})

				.whenEnter(new State("2", "start_2", flow, params),
						new ContextHandler<StatefulContext>() {
							@Override
							public void call(StatefulContext context) {
								actualOrder.add(3);
								context.trigger(new Event("2"));
							}
						})

				.whenLeave(new State("2", "start_2", flow, params),
						new ContextHandler<StatefulContext>() {
							@Override
							public void call(StatefulContext context) {
								actualOrder.add(4);
							}
						})

				.whenEnter(new State("3", "start_2", flow, params),
						new ContextHandler<StatefulContext>() {
							@Override
							public void call(StatefulContext context) {
								actualOrder.add(5);
							}
						})

				.whenLeave(new State("3", "start_2", flow, params),
						new ContextHandler<StatefulContext>() {
							@Override
							public void call(StatefulContext context) {
								throw new RuntimeException(
										"It never leaves the final state");
							}
						})

				.whenFinalState(new StateHandler<StatefulContext>() {
					@Override
					public void call(State state, StatefulContext context) {
						actualOrder.add(6);
					}
				});

		StatefulContext ctx = new StatefulContext();
		flow.instance().trace().start(ctx);
		// ctx.awaitTermination();

		int i = 0;
		for (Integer order : actualOrder) {
			i++;
			if (order != i) {
				throw new RuntimeException(
						"Events called not in order expected: " + i
								+ " actual: " + order);
			}
		}
	}

	/**
	 * 事件重利用测试
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void testEventReuse() {

		FlowBuilder flowBuilder = new FlowBuilder();

		EasyFlow<StatefulContext> flow = flowBuilder.from("flow_1", "testFlow",
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
						.to("3", "state_3", params)
						.transit(
								flowBuilder.on(new Event("2"), flow).finish(
										"4", "state_4", params),
								flowBuilder.on(new Event("1"), flow).finish(
										"5", "state_5", params)));

		flow.whenEnter(new State("1", "start_state", flow, params),
				new ContextHandler<StatefulContext>() {
					@Override
					public void call(StatefulContext context) throws Exception {
						context.trigger(new Event("2"));
					}
				}).whenEnter(new State("3", "state_3", flow, params),
				new ContextHandler<StatefulContext>() {
					@Override
					public void call(StatefulContext context) throws Exception {
						context.trigger(new Event("2"));
					}
				});

		StatefulContext ctx = new StatefulContext();
		flow.instance().trace().start(ctx);
		// ctx.awaitTermination();

		assertEquals("Final state", new State("4", "state_4", flow, params),
				ctx.getState());
	}

	/**
	 * 执行测试
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void testSyncExecutor() {
		FlowBuilder flowBuilder = new FlowBuilder();

		EasyFlow<StatefulContext> flow = flowBuilder.from("flow_1", "testFlow",
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
						.to("3", "state_3", params)
						.transit(
								flowBuilder.on(new Event("2"), flow).finish(
										"4", "state_4", params),
								flowBuilder.on(new Event("1"), flow).finish(
										"5", "state_5", params)));

		flow.whenEnter(new State("1", "start_state", flow, params),
				new ContextHandler<StatefulContext>() {
					@Override
					public void call(StatefulContext context) throws Exception {
						context.trigger(new Event("2"));
					}
				}).whenEnter(new State("3", "state_3", flow, params),
				new ContextHandler<StatefulContext>() {
					@Override
					public void call(StatefulContext context) throws Exception {
						context.trigger(new Event("2"));
					}
				});

		StatefulContext ctx = new StatefulContext();
		flow.instance().trace().start(ctx);

		assertEquals("Final state", new State("4", "state_4", flow, params),
				ctx.getState());
		assertTrue(ctx.isTerminated());
	}

	/**
	 * 上下文测试
	 */
	@Test
	public void testContext() {
		FlowBuilder<FlowMapTestContext> flowBuilder = new FlowBuilder<FlowMapTestContext>();

		EasyFlow<FlowMapTestContext> flow = flowBuilder.from("flow_1",
				"testFlow", "1", "start_state", params);

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
						context.getParamMap().put("key", "val");
						context.trigger(new Event("2"));
					}
				}).whenEnter(new State("3", "state_3", flow, params),
				new ContextHandler<FlowMapTestContext>() {
					@Override
					public void call(FlowMapTestContext context)
							throws Exception {
						assertEquals("val", context.getParamMap().get("key"));
						context.trigger(new Event("2"));
					}
				});
		FlowMapTestContext ctx = new FlowMapTestContext();
		flow.instance().trace().start(ctx);
		assertEquals("val", ctx.getParamMap().get("key"));
	}

	/**
	 * 测试挂起和continue
	 */
	@SuppressWarnings({ "unchecked" })
	@Test
	public void testSuspandAndContinue() {
		FlowBuilder<FlowMapTestContext> flowBuilder = new FlowBuilder<FlowMapTestContext>();
		EasyFlow<FlowMapTestContext> flow = flowBuilder.from("flow_1",
				"testFlow", "1", "start_state", params);
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
						context.getParamMap().put("key", "val");
						context.trigger(new Event("2"));
					}
				}).whenEnter(new State("3", "state_3", flow, params),
				new ContextHandler<FlowMapTestContext>() {
					@Override
					public void call(FlowMapTestContext context)
							throws Exception {
						assertEquals("val", context.getParamMap().get("key"));
						context.trigger(new Event("2"));
					}
				});

		FlowMapTestContext ctx = new FlowMapTestContext();
		FlowInstance<FlowMapTestContext> instance = flow.instance();
		instance.trace().start(ctx);

		assertEquals("val", ctx.getParamMap().get("key"));
		assertEquals("3", ctx.getState().getId());
		ctx.getParamMap().put("key2", "val2");
		assertTrue(!ctx.isTerminated());
		ctx.getFlowInstance().toContinue(ctx);
		assertEquals("val2", ctx.getParamMap().get("key2"));
		assertTrue(ctx.isTerminated());
	}
}
