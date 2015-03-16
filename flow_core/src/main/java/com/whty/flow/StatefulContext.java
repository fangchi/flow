package com.whty.flow;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 状态机上下文
 * @author andrey
 *
 */
@SuppressWarnings("rawtypes")
public class StatefulContext implements Serializable {
	private static final long serialVersionUID = 2324535129909715649L;
	
	//上下文id
	private String id;
	//所属流程末班
	@SuppressWarnings("unused")
	private EasyFlow flow;
	//所属流程实例
	private FlowInstance flowInstance;
	//所属状态
	private State state;
	//父流程状态
	private State parentState;
	//是否终止标识符
	private boolean terminated = false;
	//continue 令牌 用于执行一挂起的(suspand)流程
	private boolean continueToken =false;
	
	//用于持久化后获取可执行流程实例的引用   for persist reasons 
	private FlowInstance instanceToContinue;
	
	//constructors begin 
	public StatefulContext() {
		id = newId() + ":" + getClass().getSimpleName();
	}

	public StatefulContext(String aId) {
		id = aId + ":" + getClass().getSimpleName();
	}
	//constructors end 

	//getters and setters begin
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public void setState(State state) {
		this.state = state;
	}

	public State getState() {
		return state;
	}
	
	public State getParentState() {
		return parentState;
	}

	public void setParentState(State parentState) {
		this.parentState = parentState;
	}
	
	public void setFlow(EasyFlow<? extends StatefulContext> flow) {
		this.flow = flow;
	}
	
	
	public FlowInstance getInstanceToContinue() {
		return instanceToContinue;
	}

	public void setFlowInstance(FlowInstance<? extends StatefulContext> instance) {
		this.flowInstance = instance;
	}

	public FlowInstance getFlowInstance() {
		return flowInstance;
	}

	protected String newId() {
		return UUID.randomUUID().toString();
	}

	public boolean isTerminated() {
		return terminated;
	}

	public boolean isRunning() {
		return isStarted() && !terminated;
	}

	public boolean isStarted() {
		return state != null;
	}
	//getters and setters end
	
	/**
	 * 添加continue 令牌 ，该令牌用于消费一次suspand状态并在一次消费后失效
	 */
	public void addContinueToken(){
		this.continueToken  = true;
	}
	
	/**
	 * 消费令牌
	 * @return
	 */
	public boolean consumeContinueToken(){
		if(this.continueToken){
			this.continueToken = false;
			return true;
		}else{
			return false;
		}
	}
	
	public boolean isContinueToken() {
		return continueToken;
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		StatefulContext other = (StatefulContext) obj;
		if (id != other.id)
			return false;
		return true;
	}

	/**
	 * 激发事件
	 * @param event
	 */
	@SuppressWarnings("unchecked")
	public void trigger(Event event) {
		this.flowInstance.trigger(event, this);
	}

	/**
	 * 持久化
	 * @return
	 */
	public Map<String, Object> persist() {
		Map<String, Object> dataMap = new HashMap<String, Object>();
		//设置数据属性
		dataMap.put("id", this.getId());
		dataMap.put("state", this.getState().getId());
		dataMap.put("flow", this.getFlowInstance().getFlow().getId());
		dataMap.put("trace", this.getFlowInstance().isTrace());
		//设置父流程属性
		if (this.getParentState() != null)
			dataMap.put("parent_state", this.getParentState().getId());
		if (this.flowInstance.getParent() != null)
			dataMap.put("parent_flow", this.getFlowInstance().getParent().getFlow().getId());
		return dataMap;
	}
	
	/**
	 * 获取对应数据
	 * @param dataMap
	 * @param flow
	 * @param parentFlow
	 */
	@SuppressWarnings("unchecked")
	public void load(Map<String, Object> dataMap,EasyFlow flow,EasyFlow parentFlow) {
		//从数据Map中获取数据并回置对象
		this.setId(String.valueOf(dataMap.get("id")));
		this.setFlow(flow);
		this.setState(flow.getState(String.valueOf(dataMap.get("state"))));
		//获取对应的流程实例
		FlowInstance instance = flow.instance();
		boolean trace = (dataMap.get("trace")!= null && (Boolean)dataMap.get("trace"));
		this.flowInstance = instance;
		//获取要继续的流程实例 这里设置的目的是为了解决子流程的问题
		this.instanceToContinue = instance;
		//设置父节点引用
		if(parentFlow != null){
			FlowInstance parent_instance =  parentFlow.instance();
			if(trace)
				parent_instance.trace();
			instance.setParent(parent_instance);
			this.setParentState(parent_instance.getFlow().getState(String.valueOf(dataMap.get("parent_state"))));
			this.instanceToContinue = parent_instance;
		}
		
		if(trace)
			this.flowInstance.trace();
	}

	protected void setTerminated() {
		this.terminated = true;
	}
	
	@Override
	public String toString() {
		return id;
	}
}
