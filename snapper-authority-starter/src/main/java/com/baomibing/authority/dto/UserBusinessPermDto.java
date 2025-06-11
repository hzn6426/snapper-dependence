/**
 * Copyright (c) 2018-2023, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.authority.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 业务权限
 * 
 * @author zening
 * @since 1.0.0
 */
@JsonInclude(Include.NON_NULL)
@Data
@Accessors(chain = true)
public class UserBusinessPermDto implements Serializable {

    private String id;
    private String userId;
    private String permId;
    private String permScope;
	private Date permStartTime;
	private Date permEndTime;
	private String orgId;
    
    
    private String menuId;
    private String menuName;
    private String permName;
    private String parentId;
	private String tag;
    private List<UserBusinessPermDto> children;
    private List<String> groupEntrusts;
    private List<String> userEntrusts;
    //排除组织委托
    private List<String> exceptGroupEntrusts;
    //排除用户委托
    private List<String> exceptUserEntrusts;

}
