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

import java.io.Serializable;
import java.util.Date;

/**
 * 用户日志
 * @author zening 
 * @version v1.0
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@TableName("sys_tenant_user_log")
public class SysTenantUserLog extends MBaseModel implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @TableId(value = "ID", type = IdType.ASSIGN_ID)
    private String id;

    private String tenantId;
    /**
     * 传输地址
     */
    @TableField("exchange_url")
    private String exchangeUrl;
    /**
     * 传输方法
     */
    @TableField("exchange_method")
    private String exchangeMethod;
    /**
     * 传输名称
     */
    @TableField("exchange_name")
    private String exchangeName;
    /**
     * 传输参数
     */
    @TableField("exchange_param")
    private String exchangeParam;
    /**
     * 传输时间
     */
    @TableField("exchange_time")
    private Date exchangeTime;
    /**
     * 状态
     */
    private String state;
    /**
     * 异常消息
     */
    @TableField("exception_msg")
    private String exceptionMsg;
    
    /**
     * IP地址
     */
    @TableField("ip_address")
    private String ipAddress;

    /**
     * 执行方法
     */
    @TableField("execute_method")
    private String executeMethod;

    /**
     * 执行时间
     */
    @TableField("execute_timer")
    private Integer executeTimer;
    
    /**
     * 执行微服务名称
     */
    @TableField("execute_module_name")
    private String executeModuleName;
    /**
     * 日志类型编码
     */
    @TableField("log_type_code")
    private String logTypeCode;
    /**
     * 浏览器
     */
    @TableField("browser")
    private String browser;
    /**
     * 系统信息
     */
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
