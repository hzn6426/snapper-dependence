package com.baomibing.tool.tenant;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

/**
 * TenantFunctionFee
 *
 * @author zening 2024/10/18 09:40
 * @version 1.0.0
 **/
@Data
@Accessors(chain = true)
public class TenantFunctionFee {

    private String tenantId;
    private String exchangeName;
    private Date exchangeTime;
    private String ipAddress;
    private String os;
    private String browser;
    private String feeType;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
    private Integer requestQty = 1;
    private String exchangeUrl;
    private String exchangeMethod;
    private String functionId;
    private BigDecimal afterMoney;

}