/**
 * Copyright (c) 2018-2025, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.authority.runner;

import com.baomibing.authority.service.SysGateLimitService;
import com.baomibing.authority.service.SysRoleResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class AuthorizationCacheWarmUpRunner implements ApplicationRunner {

	@Autowired private SysRoleResourceService roleResourceService;
	@Autowired private SysGateLimitService gateLimitService;
	@Override
	public void run(ApplicationArguments args) throws Exception {
		roleResourceService.refreshPrivileges();
		gateLimitService.refreshLimitCache();
	}

}
