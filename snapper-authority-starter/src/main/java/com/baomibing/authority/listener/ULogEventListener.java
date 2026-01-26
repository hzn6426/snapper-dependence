
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

package com.baomibing.authority.listener;


import com.baomibing.authority.dto.HmacLogDto;
import com.baomibing.authority.dto.SysTenantUserLogDto;
import com.baomibing.authority.dto.UserLogDto;
import com.baomibing.authority.service.SysHmacLogService;
import com.baomibing.authority.service.SysTenantUserLogService;
import com.baomibing.authority.service.SysUserLogService;
import com.baomibing.tool.util.URLUtil;
import com.baomibing.web.event.HmacUserLogEvent;
import com.baomibing.web.event.TenantUserLogEvent;
import com.baomibing.web.event.UserLogEvent;
import com.github.dozermapper.core.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

/**
 * 用户日志监听，监听日志，写入日志
 * 
 * @author zening
 * @since 1.0.0
 */
@Component
@Scope(value = WebApplicationContext.SCOPE_REQUEST)
public class ULogEventListener {

	@Autowired private SysUserLogService userLogService;
    @Autowired private SysTenantUserLogService tenantUserLogService;
    @Autowired private SysHmacLogService hmacLogService;
	@Autowired private Mapper mapper;
    @Value("${spring.application.name}")
    private String serviceName;

    @Async
    @EventListener
    public void handleUserLogEvent(UserLogEvent event) {
        if (event == null) {
            return;
        }
        String cuserCnName = event.getCreateUserCnName();
        String uuserCnName = event.getUpdateUserCnName();
        cuserCnName = URLUtil.decode(URLUtil.decode(cuserCnName));
        uuserCnName = URLUtil.decode(URLUtil.decode(uuserCnName));
        if (event instanceof HmacUserLogEvent) {
            HmacLogDto ul = mapper.map(event, HmacLogDto.class);
            ul.setCreateUserCnName(cuserCnName).setUpdateUserCnName(uuserCnName);
            ul.setDataFrom(event.getOuterSystem()).setDataTo(serviceName).setDataContent(event.getResponseData());
            hmacLogService.doSaveLogAsync(ul);
        } else if ((event instanceof TenantUserLogEvent)) {
            SysTenantUserLogDto ul = mapper.map(event, SysTenantUserLogDto.class);
            ul.setCreateUserCnName(cuserCnName).setUpdateUserCnName(uuserCnName);
            tenantUserLogService.doSaveLogAsync(ul);
        } else {
            UserLogDto ul = mapper.map(event, UserLogDto.class);
            ul.setCreateUserCnName(cuserCnName).setUpdateUserCnName(uuserCnName);
            userLogService.doSaveLogAsync(ul);
        }
    }
}
