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