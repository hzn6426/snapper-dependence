/**
 * Copyright (c) 2018-2023, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.core.process;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
@SuppressWarnings("serial")
@Getter
@AllArgsConstructor
public class SProcess<S, A> implements Serializable {

	private S initState;
	private Set<S> states;
	private Set<S> editableState;
	private Set<S> deleteableState;
	private Set<A> actions;
	private List<SubProcess<S, A>> subProcesses = new ArrayList<>(16);
}
