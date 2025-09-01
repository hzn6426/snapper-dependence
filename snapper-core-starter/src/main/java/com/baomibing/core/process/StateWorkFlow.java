/**
 * Copyright (c) 2018-2025, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.core.process;

import com.baomibing.core.exception.ExceptionEnum;
import com.baomibing.core.exception.ServerRuntimeException;
import com.baomibing.tool.util.BeanUtil;
import com.baomibing.tool.util.Checker;
import lombok.Data;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 流程
 * 
 * @author zening
 * @since 1.0.0
 */
@Data
public class StateWorkFlow {
	
	public static <S extends Enum<S>, A extends Enum<A>> S doInitState(StateProcess<S, A> stateProcess) {
		return doInitState(stateProcess, null);
	}
	
	public static <S extends Enum<S>, A extends Enum<A>> S doInitState(StateProcess<S, A> stateProcess, String stateFieldName) {
		if (Checker.beNull(stateProcess)) {
			throw new ServerRuntimeException(ExceptionEnum.STATE_PROCESS_OBJECT_NOT_BE_NULL);
		}
		
		SProcess<S, A> process = stateProcess.getProcess();
		if (Checker.beNull(process) || Checker.beEmpty(process.getSubProcesses())) {
			throw new ServerRuntimeException(ExceptionEnum.PROCESS_OF_OBJECT_NOT_BE_NULL);
		}
		
		S initState = process.getInitState();
		
		if (Checker.beNull(initState)) {
			throw new ServerRuntimeException(ExceptionEnum.PROCESS_STATE_OBJECT_CANNOT_INIT_STATE);
		}
		
		final String STATE = "state";
		
		if (Checker.beEmpty(stateFieldName)) {
			stateFieldName = STATE;
		}
		if (!BeanUtil.hasPrperty(stateProcess, stateFieldName)) {
			throw new ServerRuntimeException(ExceptionEnum.PROCESS_STATE_OBJECT_NOT_HAVE_THE_STATE_FIELD_NAME, stateFieldName);
		}
		Object o = BeanUtil.getProperty(stateProcess, stateFieldName);
		String state = Checker.beNull(o) ? "" : o.toString();
		if (Checker.beNotEmpty(state)) {
			throw new ServerRuntimeException(ExceptionEnum.PROCESS_STATE_OBJECT_HAS_AN_STATE_VALUE_CANNOT_SET_INIT_STATE);
		}
		BeanUtil.setProperty(stateProcess, stateFieldName, initState.toString());
		return initState;
		
	}
	
	public static <S extends Enum<S>, A extends Enum<A>> void doAssertEdit(StateProcess<S, A> stateProcess) {
		boolean editable = checkEditable(stateProcess, null);
		if (!editable) {
			throw new ServerRuntimeException(ExceptionEnum.CURRENT_STATE_OF_OBJECT_CANNOT_EDIT);
		}
	}
	
	public static <S extends Enum<S>, A extends Enum<A>> boolean checkEditable(StateProcess<S, A> stateProcess) {
		return checkEditable(stateProcess, null);
	}
	
	public static <S extends Enum<S>, A extends Enum<A>> boolean checkEditable(StateProcess<S, A> stateProcess, String stateFieldName) {
		if (Checker.beNull(stateProcess)) {
			throw new ServerRuntimeException(ExceptionEnum.STATE_PROCESS_OBJECT_NOT_BE_NULL);
		}
		SProcess<S, A> process = stateProcess.getProcess();
		if (Checker.beNull(process) || Checker.beEmpty(process.getSubProcesses())) {
			throw new ServerRuntimeException(ExceptionEnum.PROCESS_OF_OBJECT_NOT_BE_NULL);
		}
		final String STATE = "state";
		if (Checker.beEmpty(stateFieldName)) {
			stateFieldName = STATE;
		}
		if (!BeanUtil.hasPrperty(stateProcess, stateFieldName)) {
			throw new ServerRuntimeException(ExceptionEnum.PROCESS_STATE_OBJECT_NOT_HAVE_THE_STATE_FIELD_NAME, stateFieldName);
		}
		Object o = BeanUtil.getProperty(stateProcess, stateFieldName);
		String state = Checker.beNull(o) ? "" : o.toString();
		if (Checker.beEmpty(state)) {
			throw new ServerRuntimeException(ExceptionEnum.CURRENT_STATE_OF_PRCESS_STATE_OBJECT_IS_NULL);
		}
		Set<S> editableStates = process.getEditableState();
		return editableStates.stream().anyMatch(s -> s.toString().equals(state));
		
	}
	
	public static <S extends Enum<S>, A extends Enum<A>> void doAssertDelete(StateProcess<S, A> stateProcess) {
		boolean editable = checkDeleteable(stateProcess, null);
		if (!editable) {
			throw new ServerRuntimeException(ExceptionEnum.CURRENT_STATE_OF_OBJECT_CANNOT_DELETE);
		}
	}
	
	public static <S extends Enum<S>, A extends Enum<A>> boolean checkDeleteable(StateProcess<S, A> stateProcess) {
		return checkDeleteable(stateProcess, null);
	}
	
	public static <S extends Enum<S>, A extends Enum<A>> boolean checkDeleteable(StateProcess<S, A> stateProcess, String stateFieldName) {
		if (Checker.beNull(stateProcess)) {
			throw new ServerRuntimeException(ExceptionEnum.STATE_PROCESS_OBJECT_NOT_BE_NULL);
		}
		SProcess<S, A> process = stateProcess.getProcess();
		if (Checker.beNull(process) || Checker.beEmpty(process.getSubProcesses())) {
			throw new ServerRuntimeException(ExceptionEnum.PROCESS_OF_OBJECT_NOT_BE_NULL);
		}
		final String STATE = "state";
		if (Checker.beEmpty(stateFieldName)) {
			stateFieldName = STATE;
		}
		if (!BeanUtil.hasPrperty(stateProcess, stateFieldName)) {
			throw new ServerRuntimeException(ExceptionEnum.PROCESS_STATE_OBJECT_NOT_HAVE_THE_STATE_FIELD_NAME, stateFieldName);
		}
		Object o = BeanUtil.getProperty(stateProcess, stateFieldName);
		String state = Checker.beNull(o) ? "" : o.toString();
		if (Checker.beEmpty(state)) {
			throw new ServerRuntimeException(ExceptionEnum.CURRENT_STATE_OF_PRCESS_STATE_OBJECT_IS_NULL);
		}
		Set<S> editableStates = process.getDeleteableState();
		return editableStates.stream().anyMatch(s -> s.toString().equals(state));
		
	}
	
	public static <S extends Enum<S>, A extends Enum<A>> S doProcess(StateProcess<S, A> stateProcess, A action) {
		return doProcess(stateProcess, action, null);
	}
	
	public static <S extends Enum<S>, A extends Enum<A>> S doProcess(StateProcess<S, A> stateProcess, A action, String stateFieldName) {
		final String STATE = "state";
		if (Checker.beNull(stateProcess)) {
			throw new ServerRuntimeException(ExceptionEnum.STATE_PROCESS_OBJECT_NOT_BE_NULL);
		}
		if (Checker.beNull(action)) {
			throw new ServerRuntimeException(ExceptionEnum.PROCESS_ACTION_NOT_BE_NULL);
		}
		SProcess<S, A> process = stateProcess.getProcess();
		if (Checker.beNull(process) || Checker.beEmpty(process.getSubProcesses())) {
			throw new ServerRuntimeException(ExceptionEnum.PROCESS_OF_OBJECT_NOT_BE_NULL);
		}
		
		Set<A> actions = process.getActions();
		
		if (!actions.contains(action)) {
			throw new ServerRuntimeException(ExceptionEnum.THE_ACTION_MUST_BE_IN_OBJECT_STATE_PROCESS_ACTIONS, action.toString());
		}
		
		if (Checker.beEmpty(stateFieldName)) {
			stateFieldName = STATE;
		}
		if (!BeanUtil.hasPrperty(stateProcess, stateFieldName)) {
			throw new ServerRuntimeException(ExceptionEnum.PROCESS_STATE_OBJECT_NOT_HAVE_THE_STATE_FIELD_NAME, stateFieldName);
		}
		Object o = BeanUtil.getProperty(stateProcess, stateFieldName);
		String state = Checker.beNull(o) ? "" : o.toString();
		if (Checker.beEmpty(state)) {
			throw new ServerRuntimeException(ExceptionEnum.CURRENT_STATE_OF_PRCESS_STATE_OBJECT_IS_NULL);
		}
		Set<S> states = process.getStates();
		Boolean beContains = Boolean.FALSE;
		for (S s : states) {
			if (state.equals(s.toString())) {
				beContains = Boolean.TRUE;
				break;
			}
		}
		if (!beContains) {
			throw new ServerRuntimeException(ExceptionEnum.STATES_LIST_NOT_HAVE_THE_STATE, state);
		}
		
		List<SubProcess<S, A>> subProcesses = process.getSubProcesses().stream()
				.filter(p -> p.getSource().getState().toString().equalsIgnoreCase(state)).collect(Collectors.toList());
		Optional<SubProcess<S,A>> optional = subProcesses.stream().
				filter(p -> p.getAction().getTask().toString().equalsIgnoreCase(action.toString())).findFirst();
		if (!optional.isPresent()) {
			throw new ServerRuntimeException(ExceptionEnum.CURRENT_STATE_CANNOT_EXECUTE_THE_ACTION_PROCESS);
		}
		S target = optional.get().getTarget().getState();
		BeanUtil.setProperty(stateProcess, stateFieldName, target.toString());
		return target;
	}
	
	
	
}
