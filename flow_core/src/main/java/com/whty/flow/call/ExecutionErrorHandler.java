package com.whty.flow.call;

import com.whty.flow.StatefulContext;
import com.whty.flow.err.ExecutionError;

/**
 * 执行异常handler 
 * @param <C>
 */
public interface ExecutionErrorHandler<C extends StatefulContext> extends
		Handler {
	void call(ExecutionError error, C context);
}
