/**
 * Copyright (c) 2018-2025, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.authority.wrap;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * MenuWrap
 *
 * @author zening 2022/3/7 14:38
 * @version 1.0.0
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Accessors(chain = true)
public class MenuWrap {
    private boolean disabled;
    private String key;
    private String value;
    private String title;
    private String url;
    private String parentId;
    private String parentGroupName;
    private String parentMenuType;
    private String tag;
    private String iconCls;
    private String state;
    private Boolean isLeaf;
    private Boolean selectable;
    private Boolean disableCheckbox;
    private String reqMethod;
    private String reqUrl;
    private Short priority;
    private Boolean beHidden;
    private String menuType;
    private Boolean beUnauth;
    private List<MenuWrap> children;

}
