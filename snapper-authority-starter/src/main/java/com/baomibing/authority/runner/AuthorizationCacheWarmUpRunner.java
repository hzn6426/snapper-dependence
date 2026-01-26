
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

package com.baomibing.authority.runner;

import com.baomibing.authority.service.SysBusinessActionService;
import com.baomibing.authority.service.SysGateLimitService;
import com.baomibing.authority.service.SysHmacUserService;
import com.baomibing.authority.service.SysRoleResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class AuthorizationCacheWarmUpRunner implements ApplicationRunner {

	@Autowired private SysRoleResourceService roleResourceService;
	@Autowired private SysHmacUserService hmacUserService;
	@Autowired private SysGateLimitService gateLimitService;
	@Autowired private SysBusinessActionService businessActionService;
	
	@Override
	public void run(ApplicationArguments args) throws Exception {
		roleResourceService.refreshPrivileges();
		hmacUserService.refreshPrivileges();
		gateLimitService.refreshLimitCache();
		businessActionService.refreshAllActionCache();
	}

}
