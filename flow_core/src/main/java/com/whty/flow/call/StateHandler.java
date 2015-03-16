package com.whty.flow.call;

import com.whty.flow.State;
import com.whty.flow.StatefulContext;

/**
 * 状态handler
 * @param <C>
 */
public interface StateHandler<C extends StatefulContext> extends Handler {
	void call(State state, C context) throws Exception;
}
