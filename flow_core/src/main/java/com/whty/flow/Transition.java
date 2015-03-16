package com.whty.flow;

import java.util.ArrayList;
import java.util.List;

/**
 * Transition Bean
 * @author andrey
 */
public final class Transition {
	
	//子transitions
	private static ThreadLocal<List<Transition>> transitions = new ThreadLocal<List<Transition>>();
	//事件
    private Event event;
    //状态from
    private State stateFrom;
    //状态to
    private State stateTo;
    //是否结束
    private boolean isFinal;

    //constructors begin
    protected Transition(Event event, State stateTo, boolean isFinal) {
        this.event = event;
        this.stateTo = stateTo;
        this.isFinal = isFinal;
        register(this);
    }
    
    protected Transition(Event event, State stateTo,EasyFlow<? extends StatefulContext> subFlow, boolean isFinal) {
        this.event = event;
        this.stateTo = stateTo;
        this.isFinal = isFinal;
        register(this);
    }
    //constructors end

    //注册处理transition
    private static void register(Transition transition) {
        List<Transition> list = transitions.get();
        if (list == null) {
            list = new ArrayList<Transition>();
            transitions.set(list);
        }
        list.add(transition);
    }

    //getters and setters begin
    public Event getEvent() {
        return event;
    }

    public void setStateFrom(State stateFrom) {
        this.stateFrom = stateFrom;
    }

    public State getStateFrom() {
        return stateFrom;
    }

    public State getStateTo() {
        return stateTo;
    }

    public boolean isFinal() {
        return isFinal;
    }
    //getters and setters end

    //构建transitions
    public Transition transit(Transition... transitions) {
        for (Transition transition : transitions) {
            transition.setStateFrom(stateTo);
        }
        return this;
    }

    @Override
    public String toString() {
        return "Transition{" +
            "event=" + event +
            ", stateFrom=" + stateFrom +
            ", stateTo=" + stateTo +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Transition that = (Transition) o;

        if (!event.equals(that.event)) return false;
        if (!stateFrom.equals(that.stateFrom)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = event.hashCode();
        result = 31 * result + stateFrom.hashCode();
        return result;
    }

    /**
     * 执行transitions
     * @return
     */
    protected static List<Transition> consumeTransitions() {
        List<Transition> ts = transitions.get();
        transitions.remove();
        return ts;
    }
}