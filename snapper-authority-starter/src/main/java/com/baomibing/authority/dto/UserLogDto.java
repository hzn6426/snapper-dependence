
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

package com.baomibing.authority.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 用户日志
 * 
 * @author zening
 * @version  1.0.0
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Accessors(chain = true)
public class UserLogDto {
    private String id;
    private String exchangeUrl;
    private String exchangeMethod;
    private String exchangeName;
    private String exchangeParam;
    private Date exchangeTime;
    private String state;
    private String exceptionMsg;
    private String ipAddress;
    private String executeMethod;
    private Integer executeTimer;
    private String executeModuleName;
    private String createUser;
    private String updateUser;
    private String createUserCnName;
    private String updateUserCnName;
    private String logTypeCode;
    private String browser;
    private String os;
    /**
     * 返回信息
     */
    private String responseData;

    /**
     * 登录的系统
     */
    private String systemTag;

    private Date startTime;
    private Date endTime;
    
}
