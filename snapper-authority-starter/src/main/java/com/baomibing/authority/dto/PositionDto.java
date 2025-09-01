/**
 * Copyright (c) 2018-2025, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
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
