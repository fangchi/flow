package com.whty.flow.call;

import com.whty.flow.StatefulContext;

/**
 * 上下文handler
 * 
 * @param <C>
 *            上下文对象
 */
public interface ContextHandler<C extends StatefulContext> extends Handler {
	void call(C context) throws Exception;
}
