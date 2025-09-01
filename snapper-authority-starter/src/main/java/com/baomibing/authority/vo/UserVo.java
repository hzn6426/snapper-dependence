/**
 * Copyright (c) 2018-2025, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.authority.vo;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author : zening
 * @date: 2021-07-08 17:06
 * @version: 1.0.0
 */
@Data
@Accessors(chain = true)
public class UserVo {

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

    private String permId;

    private String userMobile;
    private String userEmail;
}
