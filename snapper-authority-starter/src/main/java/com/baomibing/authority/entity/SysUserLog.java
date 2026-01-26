
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

package com.baomibing.authority.entity;

import com.baomibing.orm.base.MBaseModel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 用户日志
 * 
 * @author zening
 * @version  1.0.0
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@TableName("sys_user_log")
public class SysUserLog extends MBaseModel {

    private static final long serialVersionUID = 1L;

	@TableId(type = IdType.ASSIGN_ID)
    private String id;
    @TableField("exchange_url")
    private String exchangeUrl;
    @TableField("exchange_method")
    private String exchangeMethod;
    @TableField("exchange_name")
    private String exchangeName;
    @TableField("exchange_param")
    private String exchangeParam;
    @TableField("exchange_time")
    private Date exchangeTime;
    private String state;
    @TableField("exception_msg")
    private String exceptionMsg;
    @TableField("ip_address")
    private String ipAddress;
    @TableField("execute_method")
    private String executeMethod;
    @TableField("execute_timer")
    private Integer executeTimer;
    @TableField("execute_module_name")
    private String executeModuleName;
    @TableField("log_type_code")
    private String logTypeCode;
    @TableField("browser")
    private String browser;
    @TableField("os")
    private String os;
    /**
     * 返回信息
     */
    @TableField("response_data")
    private String responseData;

    /**
     * 登录的系统
     */
    @TableField("system_tag")
    private String systemTag;


}
