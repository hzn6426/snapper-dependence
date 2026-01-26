/*
 * Copyright (c) 2020-2025, zening (316279828@qq.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
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
