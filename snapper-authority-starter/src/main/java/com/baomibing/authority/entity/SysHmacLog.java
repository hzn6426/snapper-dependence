/**
 * Copyright (c) 2018-2023, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
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

}
