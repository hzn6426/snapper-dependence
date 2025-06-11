package com.baomibing.tool.user;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * TenantUser
 *
 * @author zening
 * @version 1.0.0
 **/
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
public class TenantUser extends User {

    private String tenantId;
}
