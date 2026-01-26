
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

import com.baomibing.authority.dto.HmacLogDto;
import com.baomibing.authority.entity.SysHmacLog;
import com.baomibing.authority.mapper.SysHmacLogMapper;
import com.baomibing.authority.service.SysHmacLogService;
import com.baomibing.core.common.SearchResult;
import com.baomibing.orm.base.MBaseServiceImpl;
import com.baomibing.tool.util.Checker;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * SysHmacLogServiceImpl
 *
 * @author zening
 * @version 1.0.0
 */
@Service
public class SysHmacLogServiceImpl extends MBaseServiceImpl<SysHmacLogMapper, SysHmacLog, HmacLogDto> implements SysHmacLogService {

    @Override
    public SearchResult<HmacLogDto> searchLog(HmacLogDto log, int pageNo, int pageSize) {
        LambdaQueryWrapper<SysHmacLog> logWrapper = lambdaQuery();
        logWrapper.like(Checker.beNotEmpty(log.getDataFrom()), SysHmacLog::getDataFrom, log.getDataFrom()).eq(Checker.beNotEmpty(log.getState()), SysHmacLog::getState, log.getState())
                .like(Checker.beNotEmpty(log.getExchangeName()), SysHmacLog::getExchangeName, log.getExchangeName())
                .ge(Checker.beNotNull(log.getStartTime()), SysHmacLog::getExchangeTime, log.getStartTime())
                .le(Checker.beNotNull(log.getEndTime()), SysHmacLog::getExchangeTime, log.getEndTime());

        return search(logWrapper, pageNo, pageSize);
    }

    @Async
    @Override
    public void doSaveLogAsync(HmacLogDto log) {
        super.saveIt(log);
    }
}
