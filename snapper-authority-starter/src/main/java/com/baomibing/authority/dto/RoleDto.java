/**
 * Copyright (c) 2018-2025, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
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
