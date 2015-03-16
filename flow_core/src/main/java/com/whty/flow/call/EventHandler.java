package com.whty.flow.call;

import com.whty.flow.Event;
import com.whty.flow.State;
import com.whty.flow.StatefulContext;

/**
 * 事件handler
 * @param <C>
 */
public interface EventHandler<C extends StatefulContext> extends Handler {
	void call(Event event, State from, State to, C context) throws Exception;
}
