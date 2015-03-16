package com.whty.flow;

import com.google.common.base.*;
import com.google.common.collect.*;
import com.whty.flow.err.DefinitionError;

import java.util.*;

/**
 * TransitionCollection Bean
 * @author andrey
 */
final class TransitionCollection {

	private static final class TransitionWithEvent implements
			Predicate<Transition> {
		private Event event;

		private TransitionWithEvent(Event event) {
			this.event = event;
		}

		@Override
		public boolean apply(Transition transition) {
			return transition.getEvent().equals(event);
		}
	}

	private Multimap<State, Transition> transitionFromState = HashMultimap
			.create();
	private Set<State> finalStates = Sets.newHashSet();

	/**
	 * 处理transition集合
	 * @param transitions 等待构建的transitions
	 * @param validate 是否校验
	 */
	protected TransitionCollection(Collection<Transition> transitions,
			boolean validate) {
		//如果transitions不为空
		if (transitions != null) {
			for (Transition transition : transitions) {
				transitionFromState.put(transition.getStateFrom(), transition);
				if (transition.isFinal()) {
					finalStates.add(transition.getStateTo());
				}
			}
		}
		//如果需要交验
		if (validate) {
			if (transitions == null || transitions.isEmpty()) {
				throw new DefinitionError("No transitions defined");
			}

			Set<Transition> processedTransitions = Sets.newHashSet();
			for (Transition transition : transitions) {
				State stateFrom = transition.getStateFrom();
				if (finalStates.contains(stateFrom)) {
					throw new DefinitionError(
							"Some events defined for final State: "
									+ stateFrom.toString());
				}

				if (processedTransitions.contains(transition)) {
					throw new DefinitionError("Ambiguous transitions: "
							+ transition);
				}

				State stateTo = transition.getStateTo();
				if (!finalStates.contains(stateTo)
						&& !transitionFromState.containsKey(stateTo)) {
					throw new DefinitionError(
							"No events defined for non-final State: " + stateTo);
				}

				// for build reasons 删除循环构建异常
				// if (stateFrom.equals(stateTo)) {
				// throw new DefinitionError("Circular transition: "
				// + transition);
				// }

				processedTransitions.add(transition);
			}
		}
	}

	/**
	 * 获取对应state下对应event的Transition
	 * @param stateFrom 状态起始
	 * @param event
	 * @return
	 */
	public Optional<Transition> getTransition(State stateFrom, Event event) {
		return FluentIterable.from(transitionFromState.get(stateFrom))
				.firstMatch(new TransitionWithEvent(event));
	}

	/**
	 * 状态是否结束
	 * @param state
	 * @return
	 */
	protected boolean isFinal(State state) {
		return finalStates.contains(state);
	}
}
