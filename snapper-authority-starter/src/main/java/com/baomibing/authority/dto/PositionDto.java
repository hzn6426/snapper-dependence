
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

package com.baomibing.authority.dto;

import com.baomibing.authority.action.PositionAction;
import com.baomibing.authority.state.PositionState;
import com.baomibing.core.process.SProcess;
import com.baomibing.core.process.StateProcess;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.EnumSet;
import java.util.List;

import static com.baomibing.authority.action.PositionAction.STOP;
import static com.baomibing.authority.action.PositionAction.USE;
import static com.baomibing.authority.state.PositionState.ACTIVE;
import static com.baomibing.authority.state.PositionState.UNACTIVE;

/**
 * 职位管理
 * 
 * @author zening
 * @version  1.0.0
 */
@JsonInclude(Include.NON_NULL)
@Data @Accessors(chain = true) 
@EqualsAndHashCode(callSuper = true)
public class PositionDto extends StateProcess<PositionState, PositionAction> implements Serializable {
	private String id;
	private String postName;
	@NotBlank(message = "所在组织不能为空")
	private String orgId;
	private String permScope;
	private String state;
	private String note;
	private String groupId;
	private Boolean beManager;
	private Boolean beLock;
	//当权限选择范围为自定义时，传入的自定义委托信息
	private List<String> entrusts;

	@Override
	public SProcess<PositionState, PositionAction> initProcess() {
		return builder
				.initState(ACTIVE).editable(EnumSet.of(ACTIVE, UNACTIVE)).deleteable(UNACTIVE)
				.process().source(ACTIVE).target(UNACTIVE).action(STOP)
				.and()
				.process().source(UNACTIVE).target(ACTIVE).action(USE)
				.build();
	}
}
