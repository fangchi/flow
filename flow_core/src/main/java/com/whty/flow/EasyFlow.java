package com.whty.flow;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.whty.flow.HandlerCollection.EventType;
import com.whty.flow.call.ContextHandler;
import com.whty.flow.call.DefaultErrorHandler;
import com.whty.flow.call.EventHandler;
import com.whty.flow.call.ExecutionErrorHandler;
import com.whty.flow.call.StateHandler;

/**
 * EasyFlow 流程模板
 * @param <C>
 */
public class EasyFlow<C extends StatefulContext> {
	static Logger log = LoggerFactory.getLogger(EasyFlow.class);
	/*其实状态*/
	private State startState;
	/*transition*/
	private TransitionCollection transitions;
	/*处理handlers 包含 state event error*/
	private HandlerCollection handlers = new HandlerCollection();
	/*流程模板id*/
	private String id;
	/*流程模板名称*/
	private String name;
	/*流程整体参数*/
	private Map<String, Object> params = new HashMap<String, Object>();
	/*流程状态*/
	private HashMap<String, State> states = new HashMap<String, State>();

	/**
	 * constructor 构建流程模板
	 * @param flow_id 流程实例
	 * @param flow_name 流程名称
	 * @param startState 流程起始状态
	 */
	protected EasyFlow(String flow_id, String flow_name, State startState) {
		//初始化对应属性
		this.id = flow_id;
		this.name = flow_name;
		this.startState = startState;
		// set state owner flow
		this.startState.setFlow(this);
		//设置默认异常handler
		this.handlers.setHandler(HandlerCollection.EventType.ERROR, null, null,
				new DefaultErrorHandler());
	}

	//getters and setters start 
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setParams(Map<String, Object> params) {
		this.params = params;
	}

	public Object getParam(String key) {
		if (this.params != null) {
			return params.get(key);
		} else {
			return null;
		}
	}

	public void addState(State state) {
		this.states.put(state.getId(), state);
		state.setFlow(this);
	}

	public State getState(String state_id) {
		return this.states.get(state_id);
	}
	//getters and setters end 
	
	/**
	 * 构建所有transition
	 * @param skipValidation
	 */
	protected void processAllTransitions(boolean skipValidation) {
		transitions = new TransitionCollection(Transition.consumeTransitions(),
				!skipValidation);
	}

	/**
	 * 实例化流程 
	 * @return
	 */
	public FlowInstance<C> instance() {
		//指定流程模板
		return new FlowInstance<C>(this);
	}

	/**
	 * 实例化子流程
	 * @param parent
	 * @return
	 */
	public FlowInstance<C> instance(FlowInstance<C> parent) {
		FlowInstance<C> instance = new FlowInstance<C>(this);
		//设置父流程
		instance.setParent(parent);
		return instance;
	}

	/**
	 * 设置指定事件处理的handler
	 * @param event 事件
	 * @param onEvent 对应的处理handler
	 * @return
	 */
	public EasyFlow<C> whenEvent(Event event,
			ContextHandler<C> onEvent) {
		handlers.setHandler(EventType.EVENT_TRIGGER, null, event, onEvent);
		return (EasyFlow<C>) this;
	}

	/**
	 * 设置所有事件的handler
	 * @param onEvent
	 * @return
	 */
	public EasyFlow<C> whenEvent(
			EventHandler<C> onEvent) {
		handlers.setHandler(EventType.EVENT_TRIGGER, null, null, onEvent);
		return (EasyFlow<C>) this;
	}

	/**
	 * 指定状态进入handler
	 * @param state
	 * @param onEnter
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <C1 extends StatefulContext> EasyFlow<C1> whenEnter(State state,
			ContextHandler<C1> onEnter) {
		handlers.setHandler(EventType.STATE_ENTER, state, null, onEnter);
		return (EasyFlow<C1>) this;
	}

	/**
	 * 状态进入
	 * @param onEnter
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <C1 extends StatefulContext> EasyFlow<C1> whenEnter(
			StateHandler<C1> onEnter) {
		handlers.setHandler(EventType.STATE_ENTER, null, null, onEnter);
		return (EasyFlow<C1>) this;
	}

	/**
	 * 指定状态离开handler
	 * @param state
	 * @param onEnter
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <C1 extends StatefulContext> EasyFlow<C1> whenLeave(State state,
			ContextHandler<C1> onEnter) {
		handlers.setHandler(EventType.STATE_LEAVE, state, null, onEnter);
		return (EasyFlow<C1>) this;
	}

	/**
	 * 离开handler
	 * @param onEnter
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <C1 extends StatefulContext> EasyFlow<C1> whenLeave(
			StateHandler<C1> onEnter) {
		handlers.setHandler(EventType.STATE_LEAVE, null, null, onEnter);
		return (EasyFlow<C1>) this;
	}

	/**
	 * 异常handler
	 * @param onError
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <C1 extends StatefulContext> EasyFlow<C1> whenError(
			ExecutionErrorHandler<C1> onError) {
		handlers.setHandler(EventType.ERROR, null, null, onError);
		return (EasyFlow<C1>) this;
	}

	/**
	 * 进入final状态
	 * @param onFinalState
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <C1 extends StatefulContext> EasyFlow<C1> whenFinalState(
			StateHandler<C1> onFinalState) {
		handlers.setHandler(EventType.FINAL_STATE, null, null, onFinalState);
		return (EasyFlow<C1>) this;
	}

	/**
	 * 获取所有handler
	 * @return
	 */
	public HandlerCollection getHandlers() {
		return handlers;
	}

	/**
	 * 获取transitions
	 * @return
	 */
	public TransitionCollection getTransitions() {
		return transitions;
	}

	/**
	 * 获取起始start
	 * @return
	 */
	public State getStartState() {
		return startState;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		EasyFlow other = (EasyFlow) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
}
