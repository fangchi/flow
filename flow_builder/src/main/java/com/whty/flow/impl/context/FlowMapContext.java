package com.whty.flow.impl.context;

import java.util.HashMap;
import java.util.Map;

import com.whty.flow.EasyFlow;
import com.whty.flow.StatefulContext;

/** 
 * 基于Map格式的流程上下文
 */
public class FlowMapContext extends StatefulContext {

	private static final long serialVersionUID = 1L;

	private Map<String, Object> paramMap = new HashMap<String, Object>();

	//operate  paramMap begin
	public void setParamMap(Map<String, Object> paramMap) {
		this.paramMap = paramMap;
	}

	public void addParam(String key, Object val) {
		this.paramMap.put(key, val);
	}

	public Object getParam(String key) {
		return paramMap.get(key);
	}
	//operate  paramMap end
	
	@Override
	public Map<String, Object> persist() {
		Map<String, Object> dataMap = super.persist();
		//持久化Map
		dataMap.put("params", this.paramMap);
		return dataMap;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void load(Map<String, Object> dataMap, EasyFlow flow,
			EasyFlow parentflow) {
		super.load(dataMap, flow, parentflow);
		//加载
		paramMap.putAll((Map<String, String>) dataMap.get("params"));
	}
}
