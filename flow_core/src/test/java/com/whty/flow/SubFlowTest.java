package com.whty.flow;

import java.util.HashMap;

import org.junit.Test;

import com.whty.flow.call.ContextHandler;

import junit.framework.TestCase;

/**
 * 子流程测试
 */
public class SubFlowTest extends TestCase{

	private HashMap<String, Object> params = new HashMap<String, Object>();

	/**
	 * 标准子流程测试
	 */
	@Test
	public void testSubFlow(){
		FlowBuilder<FlowMapTestContext> flowBuilder = new FlowBuilder<FlowMapTestContext>();
		
		EasyFlow<FlowMapTestContext> flow_sub = flowBuilder.from("flow_sub","子流程", "1",
				"sub_start_state",params);
		
		flowBuilder.transit(
				flow_sub,
				flowBuilder
						.on(new Event("2"), flow_sub)
						.to("3", "sub_state_3",params)
						.transit(
								flowBuilder.on(new Event("2"), flow_sub).finish(
										"4", "sub_state_4",params),
								flowBuilder.on(new Event("1"), flow_sub).finish(
										"5", "sub_state_5",params)));
		
		flow_sub
        	.whenEnter(new State("1", "sub_start_state",flow_sub,params), new ContextHandler<StatefulContext>() {
	            @Override
	            public void call(StatefulContext context) throws Exception {
	                context.trigger(new Event("2"));
	            }
        })
        	.whenEnter(new State("3", "sub_state_3",flow_sub,params), new ContextHandler<StatefulContext>() {
	            @Override
	            public void call(StatefulContext context) throws Exception {
	                context.trigger(new Event("2"));
	            }
        });
		
		EasyFlow<FlowMapTestContext> flow = flowBuilder.from("flow_1","父流程", "1",
				"start_state",params);
		
		flowBuilder.transit(
				flow,
				flowBuilder
						.on(new Event("2"), flow)
						.to("3", "state_3",flow_sub,params)
						.transit(
								flowBuilder.on(new Event("2"), flow).finish(
										"4", "state_4",params),
								flowBuilder.on(new Event("1"), flow).finish(
										"5", "state_5",params)));
		
		flow
        	.whenEnter(new State("1", "start_state",flow,params), new ContextHandler<StatefulContext>() {
	            @Override
	            public void call(StatefulContext context) throws Exception {
	                context.trigger(new Event("2"));
	            }
        })
        	.whenEnter(new State("3", "state_3",flow,params), new ContextHandler<StatefulContext>() {
            @Override
            public void call(StatefulContext context) throws Exception {
                context.trigger(new Event("2"));
            }
        });
		
		FlowMapTestContext ctx = new FlowMapTestContext();
		flow.instance().trace().start(ctx);
	}
	
	/**
	 * 子流程挂起测试
	 */
	@Test
	public void testSubFlowSuspand(){
		FlowBuilder<FlowMapTestContext> flowBuilder = new FlowBuilder<FlowMapTestContext>();
		
		EasyFlow<FlowMapTestContext> flow_sub = flowBuilder.from("flow_sub","子流程", "1",
				"sub_start_state",params);
		
		flowBuilder.transit(
				flow_sub,
				flowBuilder
						.on(new Event("2"), flow_sub)
						.to("3", "sub_state_3",true,params)
						.transit(
								flowBuilder.on(new Event("2"), flow_sub).finish(
										"4", "sub_state_4",params),
								flowBuilder.on(new Event("1"), flow_sub).finish(
										"5", "sub_state_5",params)));
		
		flow_sub
        	.whenEnter(new State("1", "sub_start_state",flow_sub,params), new ContextHandler<StatefulContext>() {
	            @Override
	            public void call(StatefulContext context) throws Exception {
	                context.trigger(new Event("2"));
	            }
        })
        	.whenEnter(new State("3", "sub_state_3",flow_sub,params), new ContextHandler<StatefulContext>() {
	            @Override
	            public void call(StatefulContext context) throws Exception {
	                context.trigger(new Event("2"));
	            }
        });
		
		EasyFlow<FlowMapTestContext> flow = flowBuilder.from("flow_1","父流程", "1",
				"start_state",params);
		
		flowBuilder.transit(
				flow,
				flowBuilder
						.on(new Event("2"), flow)
						.to("3", "state_3",flow_sub,params)
						.transit(
								flowBuilder.on(new Event("2"), flow).finish(
										"4", "state_4",params),
								flowBuilder.on(new Event("1"), flow).finish(
										"5", "state_5",params)));
		
		flow
        	.whenEnter(new State("1", "start_state",flow,params), new ContextHandler<StatefulContext>() {
	            @Override
	            public void call(StatefulContext context) throws Exception {
	                context.trigger(new Event("2"));
	            }
        })
        	.whenEnter(new State("3", "state_3",flow,params), new ContextHandler<StatefulContext>() {
            @Override
            public void call(StatefulContext context) throws Exception {
                context.trigger(new Event("2"));
            }
        });
		
		FlowMapTestContext ctx = new FlowMapTestContext();
		FlowInstance<FlowMapTestContext> instance = flow.instance();
		instance.trace().start(ctx);
		instance.toContinue(ctx);
	}
}
