package com.whty.flow;

import java.util.HashMap;
import java.util.Map;

/**
 * 状态机状态bean
 * 
 * @author andrey
 */
public class State {

	private String id;

	private String name;

	// 所属流程
	private EasyFlow<? extends StatefulContext> flow;

	// 拥有子流程
	private EasyFlow<? extends StatefulContext> subflow;

	// state-params
	private Map<String, Object> params = new HashMap<String, Object>();

	// 是否挂起
	private boolean suspand = false;

	// 是否已经没有transitions
	private boolean hasNoTransitions = false;

	// //constructors begin
	public State(String id, String name, Map<String, Object> params) {
		this(id, name, false, params);
	}

	public State(String id, String name, boolean suspand,
			Map<String, Object> params) {
		this(id, name, null, suspand, params);
	}

	public State(String id, String name, boolean suspand,
			boolean hasNoTransitions, EasyFlow<? extends StatefulContext> flow,
			Map<String, Object> params) {
		this(id, name, flow, suspand, params);
		// for build reasons
		this.hasNoTransitions = hasNoTransitions;
	}

	public State(String id, String name,
			EasyFlow<? extends StatefulContext> flow, Map<String, Object> params) {
		this(id, name, flow, false, params);
	}

	public State(String id, String name,
			EasyFlow<? extends StatefulContext> flow, boolean suspand,
			Map<String, Object> params) {
		this(id, name, flow, null, suspand, params);
	}

	public State(String id, String name,
			EasyFlow<? extends StatefulContext> flow,
			EasyFlow<? extends StatefulContext> subflow, boolean suspand,
			Map<String, Object> params) {
		super();
		this.id = id;
		this.name = name;
		this.flow = flow;
		this.subflow = subflow;
		this.suspand = suspand;
		this.params = params;
	}

	// //constructors end

	// //getters and setters beign
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

	public Object getParam(String key) {
		if (this.params != null) {
			return this.params.get(key);
		} else {
			return null;
		}
	}

	public void addParam(String key, Object val) {
		if (this.params == null) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put(key, val);
			this.params = map;
		} else {
			this.params.put(key, val);
		}
	}

	public void setFlow(EasyFlow<? extends StatefulContext> flow) {
		this.flow = flow;
	}

	public EasyFlow<? extends StatefulContext> getFlow() {
		return this.flow;
	}

	public EasyFlow<? extends StatefulContext> getSubflow() {
		return subflow;
	}

	public void setSubflow(EasyFlow<? extends StatefulContext> subflow) {
		this.subflow = subflow;
	}

	public boolean isSuspand() {
		return suspand;
	}

	public boolean isHasNoTransitions() {
		return hasNoTransitions;
	}

	public void setHasNoTransitions(boolean hasNoTransitions) {
		this.hasNoTransitions = hasNoTransitions;
	}

	// //getters and setters end

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((flow == null) ? 0 : flow.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		State other = (State) obj;
		if (flow == null) {
			if (other.flow != null)
				return false;
		} else if (!flow.equals(other.flow))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "State [id=" + id + ", name=" + name + ", flow=" + flow.getId()
				+ "]";
	}
}
