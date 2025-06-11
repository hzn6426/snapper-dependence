
package com.baomibing.web.event;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

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
public class TenantUserLogEvent extends UserLogEvent {

	private String tenantId;

    public TenantUserLogEvent(String serviceName) {
        super(serviceName);
    }
}
