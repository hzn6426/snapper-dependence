package com.baomibing.tool.tenant;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

/**
 * TenantFunctionConsume
 *
 * @author zening 2024/10/16 17:00
 * @version 1.0.0
 **/
@Data
@Accessors(chain = true)
public class TenantFunctionConsume {
    private String functionId;
    private String functionName;
    private String requestUrl;
    private String requestMethod;
    private String feeType;
    private BigDecimal unitPrice;
    private String tenantId;
    private String state;
    private Date requestTime;
    private Integer requestQty = 1;
    private BigDecimal afterMoney;

}
