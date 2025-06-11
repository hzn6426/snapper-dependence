/**
 * Copyright (c) 2018-2023, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.core.wrap;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class UserAuthWrap {

	private String id;
	private String userName;
	private String userNo;
	private String userRealCnName;
	private String userRealEnName;
	private String currentGroupId;
	private String currentPositionId;
	private String permScope;
	private String companyId;
	
	private boolean beIgnoreUserScope = false;
	private boolean beIgnoreGroupScope = false;

	private String groupId;
	private String permId;
	private String userMobile;
	private String userEmail;
}
