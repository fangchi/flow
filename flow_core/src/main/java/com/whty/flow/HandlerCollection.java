package com.whty.flow;

import java.util.Map;

import com.google.common.collect.Maps;
import com.whty.flow.call.ContextHandler;
import com.whty.flow.call.EventHandler;
import com.whty.flow.call.ExecutionErrorHandler;
import com.whty.flow.call.Handler;
import com.whty.flow.call.StateHandler;
import com.whty.flow.err.ExecutionError;

/**
 * handler处理集合
 * 
 * @author andrey
 */
class HandlerCollection {

	// 枚举事件类型
	public enum EventType {
		EVENT_TRIGGER, ANY_EVENT_TRIGGER, STATE_ENTER, ANY_STATE_ENTER, STATE_LEAVE, ANY_STATE_LEAVE, FINAL_STATE, ERROR
	}

	/**
	 * 处理器类型,用于从总体处理Map中作为Key值获取对应的值
	 * 
	 * @author andrey
	 */
	private static final class HandlerType {
		// 事件类型
		EventType eventType;
		// 事件
		Event event;
		// 状态
		State state;

		// 初始化
		private HandlerType(EventType eventType, Event event, State state) {
			this.eventType = eventType;
			this.event = event;
			this.state = state;
		}

		/**
		 * 需根据event eventType state判断是否相同
		 */
		@Override
		public boolean equals(Object o) {
			if (this == o)
				return true;
			if (o == null || getClass() != o.getClass())
				return false;

			HandlerType that = (HandlerType) o;

			if (event != null ? !event.equals(that.event) : that.event != null)
				return false;
			if (eventType != that.eventType)
				return false;
			if (state != null ? !state.equals(that.state) : that.state != null)
				return false;

			return true;
		}

		@Override
		public int hashCode() {
			int result = eventType.hashCode();
			result = 31 * result + (event != null ? event.hashCode() : 0);
			result = 31 * result + (state != null ? state.hashCode() : 0);
			return result;
		}
	}

	/* 总体handler处理Map 为日后不同场景获取对应handler */
	private Map<HandlerType, Handler> handlers = Maps.newHashMap();

	/**
	 * 设置handler
	 * 
	 * @param eventType
	 *            事件类型
	 * @param state
	 *            状态
	 * @param event
	 *            事件
	 * @param handler
	 *            处理类
	 */
	public void setHandler(EventType eventType, State state, Event event,
			Handler handler) {
		handlers.put(new HandlerType(eventType, event, state), handler);
	}

	/**
	 * 事件激发时执行对应handler
	 * 
	 * @param event
	 *            事件
	 * @param stateFrom
	 *            起始状态
	 * @param stateTo
	 *            目标状态
	 * @param context
	 *            内容
	 * @throws Exception
	 *             异常
	 */
	@SuppressWarnings("unchecked")
	public <C extends StatefulContext> void callOnEventTriggered(Event event,
			State stateFrom, State stateTo, C context) throws Exception {
		Handler h = handlers.get(new HandlerType(EventType.EVENT_TRIGGER,
				event, null));
		if (h != null) {
			ContextHandler<C> contextHandler = (ContextHandler<C>) h;
			contextHandler.call(context);
		}
		h = handlers.get(new HandlerType(EventType.ANY_EVENT_TRIGGER, null,
				null));
		if (h != null) {
			EventHandler<C> eventHandler = (EventHandler<C>) h;
			eventHandler.call(event, stateFrom, stateTo, context);
		}
	}

	/**
	 * enter状态处理
	 * @param state
	 * @param context
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public <C extends StatefulContext> void callOnStateEntered(State state,
			C context) throws Exception {
		//从handlers中获取对应数据并执行
		//率先获取当前state的handler
		Handler h = handlers.get(new HandlerType(EventType.STATE_ENTER, null,
				state));
		if (h != null) {
			ContextHandler<C> contextHandler = (ContextHandler<C>) h;
			contextHandler.call(context);
		}
		//获取普遍的
		h = handlers
				.get(new HandlerType(EventType.ANY_STATE_ENTER, null, null));
		if (h != null) {
			StateHandler<C> stateHandler = (StateHandler<C>) h;
			stateHandler.call(state, context);
		}
	}

	/**
	 * leave状态处理
	 * @param state
	 * @param context
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public <C extends StatefulContext> void callOnStateLeaved(State state,
			C context) throws Exception {
		Handler h = handlers.get(new HandlerType(EventType.STATE_LEAVE, null,
				state));
		//从handlers中获取对应数据并执行
		//率先获取当前state的handler
		if (h != null) {
			ContextHandler<C> contextHandler = (ContextHandler<C>) h;
			contextHandler.call(context);
		}
		//获取普遍的
		h = handlers
				.get(new HandlerType(EventType.ANY_STATE_LEAVE, null, null));
		if (h != null) {
			StateHandler<C> stateHandler = (StateHandler<C>) h;
			stateHandler.call(state, context);
		}
	}

	/**
	 * 执行final
	 * @param state
	 * @param context
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public <C extends StatefulContext> void callOnFinalState(State state,
			C context) throws Exception {
		Handler h = handlers.get(new HandlerType(EventType.FINAL_STATE, null,
				null));
		if (h != null) {
			StateHandler<C> contextHandler = (StateHandler<C>) h;
			contextHandler.call(state, context);
		}
	}

	/**
	 * error 执行
	 * @param state
	 * @param context
	 * @throws Exception
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void callOnError(ExecutionError error) {
		Handler h = handlers.get(new HandlerType(EventType.ERROR, null, null));
		if (h != null) {
			ExecutionErrorHandler errorHandler = (ExecutionErrorHandler) h;
			errorHandler.call(error, error.getContext());
		}
	}
}
