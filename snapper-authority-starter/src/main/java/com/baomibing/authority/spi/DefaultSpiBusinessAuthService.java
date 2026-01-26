
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

package com.baomibing.authority.spi;

import com.baomibing.authority.service.CommonBusinessAuthService;
import com.baomibing.core.common.ApplicationContextHandler;
import com.baomibing.core.spi.BusinessAuthService;
import com.baomibing.core.wrap.EntrustWarpper;
import com.baomibing.tool.user.User;
import com.google.auto.service.AutoService;

/**
 *  默认SPI业务实现
 **/
@AutoService(value = BusinessAuthService.class)
public class DefaultSpiBusinessAuthService implements BusinessAuthService {

    private CommonBusinessAuthService getBusinessAuthApi() {
        return ApplicationContextHandler.getBean(CommonBusinessAuthService.class);
    }

    @Override
    public EntrustWarpper getEntrustBusinessPerm(User user, String permId, String scope, boolean beIgnoreUserScope, boolean beIgnoreGroupScope) {
        return getBusinessAuthApi().getEntrustBusinessPerm(user, permId, scope, beIgnoreUserScope, beIgnoreGroupScope);
    }

    @Override
    public String getPermActionByUrlAndMethod(String url, String method) {
        return getBusinessAuthApi().getPermActionByUrlAndMethod(url, method);
    }

    @Override
    public String getPermIdByUrlAndMethod(String url, String method) {
        return getBusinessAuthApi().getPermIdByUrlAndMethod(url, method);
    }

    @Override
    public String getPermIdByAction(String action) {
        return getBusinessAuthApi().getPermIdByAction(action);
    }
}
