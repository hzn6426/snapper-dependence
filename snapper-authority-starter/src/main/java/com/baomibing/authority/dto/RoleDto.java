
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

import com.baomibing.authority.action.RoleAction;
import com.baomibing.authority.state.RoleState;
import com.baomibing.core.process.SProcess;
import com.baomibing.core.process.StateProcess;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.EnumSet;

import static com.baomibing.authority.action.RoleAction.STOP;
import static com.baomibing.authority.action.RoleAction.USE;
import static com.baomibing.authority.state.RoleState.ACTIVE;
import static com.baomibing.authority.state.RoleState.UNACTIVE;

/**
 * 角色对象
 * 
 * @author zening
 * @since 1.0.0
 */
@JsonInclude(Include.NON_NULL)
@Data @Accessors(chain = true) 
@EqualsAndHashCode(callSuper = true)
public class RoleDto  extends StateProcess<RoleState, RoleAction> {
	

    private String id;
    private String roleName;
    private String note;
	private String groupId;
	private String state;
	private Boolean beSuper;
	private Boolean beDefault;
	private Boolean beLock;

	private String companyName;
    private String companyId;

	private String usetNames;

	private String postNames;
	private Date createTime;
	private String createUserCnName;
	
	
	@Override
	public SProcess<RoleState, RoleAction> initProcess() {
		return builder
			.initState(UNACTIVE).deleteable(EnumSet.of(UNACTIVE))
			.process().source(UNACTIVE).target(ACTIVE).action(USE)
            .and()
			.process().source(ACTIVE).target(UNACTIVE).action(STOP)
            .build();
	}
	

}
