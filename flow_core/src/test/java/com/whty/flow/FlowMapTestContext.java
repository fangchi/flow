package com.whty.flow;

import java.util.HashMap;
import java.util.Map;

/**
 * 上下文Map
 */
public class FlowMapTestContext extends StatefulContext {
	
	private static final long serialVersionUID = 1L;
	
	//hash map
	private Map<String, Object> paramMap = new HashMap<String, Object>();

	public Map<String, Object> getParamMap() {
		return paramMap;
	}

	public void setParamMap(Map<String, Object> paramMap) {
		this.paramMap = paramMap;
	}
	
	
}
