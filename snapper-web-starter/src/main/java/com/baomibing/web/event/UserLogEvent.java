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
package com.baomibing.web.event;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.context.ApplicationEvent;

import java.util.Date;

/**
 * 用户日志事件-用于保存日志
 * 
 * @author zening
 * @since 1.0.0
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class UserLogEvent extends ApplicationEvent {

	private static final long serialVersionUID = -8628577357652686181L;
    /**
     * 传输地址
     */
    private String exchangeUrl;
    /**
     * 传输方法
     */
    private String exchangeMethod;
    /**
     * 传输名称
     */
    private String exchangeName;
    /**
     * 传输参数
     */
    private String exchangeParam;
    /**
     * 传输时间
     */
    private Date exchangeTime;
    /**
     * 状态
     */
    private String state;
    /**
     * 异常消息
     */
    private String exceptionMsg;
    /**
     * IP地址
     */
    private String ipAddress;
    /**
     * 执行方法
     */
    private String executeMethod;
    /**
     * 执行时间
     */
    private Integer executeTimer;
    /**
     * 执行的微服务名称
     */
    private String executeModuleName;
    /**
     * 创建用户
     */
    private String createUser;
    /**
     * 更新用户
     */
    private String updateUser;
    /**
     * 创建用户姓名
     */
    private String createUserCnName;
    /**
     * 更新用户姓名
     */
    private String updateUserCnName;
    /**
     * 日志类型编码
     */
    private String logTypeCode;
    /**
     * 浏览器
     */
    private String browser;
    /**
     * 系统信息
     */
    private String os;
    /**
     * 外部系统
     */
    private String outerSystem;
    /**
     * 是否是HMac请求
     */
    private Boolean beHmacRequest = false;
    /**
     * 返回的数据内容
     */
    private String responseData;
    /**
     * 登录的系统
     */
    private String systemTag;


    public UserLogEvent(String serviceName) {
        super(serviceName);
        this.executeModuleName = serviceName;
    }
}
