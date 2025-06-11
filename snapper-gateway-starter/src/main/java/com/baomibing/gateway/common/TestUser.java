/**
 * Copyright (c) 2018-2023, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.gateway.common;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * TestUser
 *
 * @author zening 2023/8/2 21:24
 * @version 1.0.0
 **/
@Data @Accessors(chain = true)
public class TestUser {

    private String id;
    private String userName;
    private String userCnName;
    private String userEnName;

    private String companyId;
    private String companyName;
    private String roles; //多个以逗号间隔
    private String positionId;
    private String groupId;
    private String groupName;
    private String userTag;//多个以逗号间隔

}
