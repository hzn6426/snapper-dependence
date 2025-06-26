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
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * 按钮
 *
 * @author zening
 * @since 1.0.0
 */
@JsonInclude(Include.NON_NULL)
@Data
@Accessors(chain = true)
@EqualsAndHashCode()
public class ButtonDto implements Serializable {
    
    private static final long serialVersionUID = 8416179840325844378L;
    
    private String id;
    @NotBlank
    private String buttonName;
    @NotNull
    private String menuId;
    private String reqMethod;
    private String reqUrl;
    private Boolean beUnauth;
    private Boolean beLoginUnauth;
    private String subMenu;
    
    private String permScope;
    private String permId;
    private Date permStartTime;
    private Date permEndTime;
    private String permAction;
    private String actionRef;
    private Boolean beLock;

    private String keyword;

    private String dataPerm;

    private String columnPerm;
    
}
