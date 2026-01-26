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

package com.baomibing.authority.service.impl;

import com.baomibing.authority.dto.SysTenantUserLogDto;
import com.baomibing.authority.entity.SysTenantUserLog;
import com.baomibing.authority.mapper.SysTenantUserLogMapper;
import com.baomibing.authority.service.SysTenantUserLogService;
import com.baomibing.core.common.Assert;
import com.baomibing.core.common.SearchResult;
import com.baomibing.orm.base.MBaseServiceImpl;
import com.google.common.collect.Lists;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.baomibing.tool.util.PageUtil.offsetCurrent;

/**
 * SysTenantUserLogServiceImpl
 *
 * @author zening
 * @version 1.0.0
 **/
@Service
public class SysTenantUserLogServiceImpl extends MBaseServiceImpl<SysTenantUserLogMapper, SysTenantUserLog, SysTenantUserLogDto> implements SysTenantUserLogService {

    @Async
    @Override
    public void doSaveLogAsync(SysTenantUserLogDto userLog) {
        Assert.CheckArgument(userLog);
        super.saveIt(userLog);
    }

    @Override
    public SearchResult<SysTenantUserLogDto> searchLog(SysTenantUserLogDto dto, int pageNo, int pageSize) {
        Assert.CheckArgument(dto);
        doSetTenantId(dto);
        int offset = offsetCurrent(pageNo, pageSize);
        Integer count = this.baseMapper.countLog(dto.getTenantId(), dto.getState(),dto.getCreateUserCnName(),dto.getExchangeName(), dto.getStartTime(), dto.getEndTime());
        if (count == 0) {
            return new SearchResult<>(0, Lists.newLinkedList());
        }
        List<SysTenantUserLog> logs = this.baseMapper.listLogs(dto.getTenantId(), dto.getState(),dto.getCreateUserCnName(),dto.getExchangeName(),dto.getStartTime(), dto.getEndTime(), offset, pageSize);
        return new SearchResult<>(count, mapper(logs));
    }
}
