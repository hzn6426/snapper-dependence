
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
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * ERP日志记录对象，用于记录ERP交互日志
 * @author zening
 * @version 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("sys_hmac_log")
public class SysHmacLog extends MBaseModel {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    private String id;
    /**
     * 数据来源
     */
    private String dataFrom;
    /**
     * 数据目的
     */
    private String dataTo;
    /**
     * 数据内容
     */
    private String dataContent;
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
     * 异常信息
     */
    private String exceptionMsg;
    /**
     * IP地址
     */
    private String ipAddress;

    private String tenantId;

    private String tenantName;

    private String bindType;

}
