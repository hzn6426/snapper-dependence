/**
 * Copyright (c) 2018-2023, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.core.process;

import com.google.common.collect.Sets;

import java.util.*;


public class ProcessBuilder {

	
	public static <S extends Enum<S>, A extends Enum<A>> Builder<S, A> create() {
		return new Builder<S, A>();
	}
	
	public static class SubProcessBuilder<S extends Enum<S>, A extends Enum<A>> {
		
		public SubProcessBuilder(Builder<S, A> builder) {
			this.builder = builder;
			this.subProcess = new SubProcess<>();
		}
		private SubProcess<S, A> subProcess;
		private Builder<S, A> builder;
		 
		public SubProcessBuilder<S, A> target(S target) {
			this.subProcess.target(target);
			return this;
		}
		
		public SubProcessBuilder<S, A> source(S source) {
			this.subProcess.source(source);
			return this;
		}
		
		public SubProcessBuilder<S, A> action(A action) {
			this.subProcess.action(action);
			return this;
		}
		
		public Builder<S, A> and() {
			return this.builder.and(subProcess);
		}
		
		public SProcess<S, A> build() {
			return this.builder.build(subProcess);
		}
	}
	
	public static class Builder<S extends Enum<S>, A extends Enum<A>> {
		
		private S initState;
		
		private Set<S> states;
		
		private Set<A> actions;
		
		private Set<S> editableState = new HashSet<>();
		
		private Set<S> deleteableState = new HashSet<>();
		
		private List<SubProcess<S, A>> processSubList = new ArrayList<>(16);
		
		public Builder<S, A> initState(S state) {
			if (state == null) throw new NullPointerException("initState not be null!");
			this.initState = state;
			return this;
		}
		
		public Builder<S, A> states(Set<S> states) {
			if (states == null) throw new NullPointerException("initState not be null!");
			this.states = states;
			this.editableState = states;
			return this;
		}
		
		public Builder<S, A> states(Class<S> actionEnumClass) {
			if (actionEnumClass == null)
				throw new NullPointerException("actionEnumClass not be null!");
			Set<S> states = EnumSet.allOf(actionEnumClass);
			this.states = states;
			this.editableState = states;
			return this;
		}

		public Builder<S, A> editable(Set<S> states) {
			this.editableState = states;
			return this;
		}
		
		@SuppressWarnings("unchecked")
		public Builder<S, A> editable(S... es) {
			Set<S> states = Sets.newHashSet(es);
			this.editableState = states;
			return this;
		}

		public Builder<S, A> deleteable(Set<S> states) {
			this.deleteableState = states;
			return this;
		}
		
		@SuppressWarnings("unchecked")
		public Builder<S, A> deleteable(S... es) {
			Set<S> states = Sets.newHashSet(es);
			this.deleteableState = states;
			return this;
		}

		public Builder<S, A> actions(Set<A> actions) {
			if (actions == null) throw new NullPointerException("initState not be null!");
			this.actions = actions;
			return this;
		}
		
		public Builder<S, A> actions(Class<A> actionEnumClass) {
			if (actionEnumClass == null) throw new NullPointerException("actionEnumClass not be null!");
			Set<A> actions = EnumSet.allOf(actionEnumClass);
			this.actions = actions;
			return this;
		}

		public SubProcessBuilder<S, A> process() {
			SubProcessBuilder<S, A> sb = new SubProcessBuilder<>(this);
			return sb;
		}
		
		public Builder<S, A> and (SubProcess<S, A> processSub) {
			this.processSubList.add(processSub);
			return this;
		}
		
		public SProcess<S, A> build(SubProcess<S, A> processSub) {
			this.processSubList.add(processSub);
			return new SProcess<S, A>(initState, states, editableState, deleteableState, actions, processSubList);
		}
	
	}
	
}
