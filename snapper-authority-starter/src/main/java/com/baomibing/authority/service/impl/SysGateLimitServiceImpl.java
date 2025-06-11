package com.baomibing.authority.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.baomibing.authority.dto.GateLimitDto;
import com.baomibing.authority.entity.SysGateLimit;
import com.baomibing.authority.mapper.SysGateLimitMapper;
import com.baomibing.authority.service.*;
import com.baomibing.authority.state.LimitState;
import com.baomibing.core.common.SearchResult;
import com.baomibing.core.process.StateWorkFlow;
import com.baomibing.orm.base.MBaseServiceImpl;
import com.baomibing.tool.constant.RedisKeyConstant;
import com.baomibing.tool.util.Checker;
import com.baomibing.tool.util.SnowflakeIdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

import static com.baomibing.authority.action.LimitAction.stop;
import static com.baomibing.authority.action.LimitAction.use;


/**
 * SysGateLimitServiceImpl
 *
 * @author zening
 * @version 1.0.0
 **/
@Service
public class SysGateLimitServiceImpl extends MBaseServiceImpl<SysGateLimitMapper, SysGateLimit, GateLimitDto> implements SysGateLimitService {

    @Autowired private SysGateLimitUtagService gateLimitUtagService;
    @Autowired private SysGateLimitStagService gateLimitStagService;
    @Autowired private SysGateLimitUserService gateLimitUserService;
    @Autowired private SysGateLimitRequestService gateLimitRequestService;
    @Autowired private SysGateLimitTuserService gateLimitTuserService;

    @Override
    public SearchResult<GateLimitDto> searchGateLimit(GateLimitDto limit, int pageNo, int pageSize) {
        return search(lambdaQuery(), pageNo, pageSize);
    }

    @Override
    public GateLimitDto getGateLimitById(String id) {
        GateLimitDto limit = getIt(id);
        if (Checker.beNull(limit)) {
            return null;
        }
        limit.setRequests(gateLimitRequestService.listByLimit(id));
        limit.setUsers(gateLimitUserService.listByLimit(id));
        limit.setUtags(gateLimitUtagService.listByLimit(id));
        limit.setStags(gateLimitStagService.listByLimit(id));
        limit.setTusers(gateLimitTuserService.listByLimit(id));
        return limit;
    }

    @Override
    public void saveGateLimit(GateLimitDto limit) {
        StateWorkFlow.doInitState(limit);
        saveIt(limit);
        if (Checker.beNotEmpty(limit.getStags())) {
            limit.getStags().forEach(s -> s.setLimitId(limit.getId()));
            gateLimitStagService.saveItBatch(limit.getStags());
        }
        if (Checker.beNotEmpty(limit.getUtags())) {
            limit.getUtags().forEach(s -> s.setLimitId(limit.getId()));
            gateLimitUtagService.saveItBatch(limit.getUtags());
        }

        if (Checker.beNotEmpty(limit.getRequests())) {
            limit.getRequests().forEach(r -> r.setLimitId(limit.getId()));
            gateLimitRequestService.saveItBatch(limit.getRequests());
        }

        if (Checker.beNotEmpty(limit.getUsers())) {
            limit.getUsers().forEach(u -> u.setLimitId(limit.getId()));
            gateLimitUserService.saveItBatch(limit.getUsers());
        }

        if (Checker.beNotEmpty(limit.getTusers())) {
            limit.getTusers().forEach(u -> u.setLimitId(limit.getId()));
            gateLimitTuserService.saveItBatch(limit.getTusers());
        }


    }

    @Override
    public void updateGateLimit(GateLimitDto limit) {
        assertBeLock(limit);
        updateIt(limit);
        if (Checker.beNotEmpty(limit.getStags())) {
            gateLimitStagService.deleteByLimit(limit.getId());
            limit.getStags().forEach(s -> s.setLimitId(limit.getId()));
            gateLimitStagService.saveItBatch(limit.getStags());
        }
        if (Checker.beNotEmpty(limit.getUtags())) {
            gateLimitUtagService.deleteByLimit(limit.getId());
            limit.getUtags().forEach(s -> s.setLimitId(limit.getId()));
            gateLimitUtagService.saveItBatch(limit.getUtags());
        }

        if (Checker.beNotEmpty(limit.getRequests())) {
            gateLimitRequestService.deleteByLimit(limit.getId());
            limit.getRequests().forEach(r -> r.setLimitId(limit.getId()));
            gateLimitRequestService.saveItBatch(limit.getRequests());
        }

        if (Checker.beNotEmpty(limit.getUsers())) {
            gateLimitUserService.deleteByLimit(limit.getId());
            limit.getUsers().forEach(u -> u.setLimitId(limit.getId()));
            gateLimitUserService.saveItBatch(limit.getUsers());
        }

        if (Checker.beNotEmpty(limit.getTusers())) {
            gateLimitTuserService.deleteByLimit(limit.getId());
            limit.getTusers().forEach(u -> u.setLimitId(limit.getId()));
            gateLimitTuserService.saveItBatch(limit.getTusers());
        }
    }

    @Override
    public void deleteLimits(Set<String> limits) {
        List<GateLimitDto> gateLimits = super.gets(limits);
        for (GateLimitDto dto : gateLimits) {
            assertBeLock(dto);
            StateWorkFlow.doAssertDelete(dto);
        }
        deletes(limits);
    }

    @Override
    public void doStop(Set<String> limits) {
        List<GateLimitDto> gateLimits = super.gets(limits);
        for (GateLimitDto dto : gateLimits) {
            assertBeLock(dto);
            StateWorkFlow.doProcess(dto, stop);
        }
        updateItBatch(gateLimits);
    }

    @Override
    public void doUse(Set<String> limits) {
//        List<GateLimitDto> list = listUsed();
//        for (GateLimitDto gateLimit : list) {
//            StateWorkFlow.doProcess(gateLimit, stop);
//        }
//        if (Checker.beNotEmpty(list)) {
//            updateItBatch(list);
//        }
        List<GateLimitDto> gateLimits = super.gets(limits);
        for (GateLimitDto dto : gateLimits) {
            assertBeLock(dto);
            StateWorkFlow.doProcess(dto, use);
        }
        updateItBatch(gateLimits);
    }

    @Override
    public List<GateLimitDto> listUsed() {
        return mapper(baseMapper.selectList(lambdaQuery().eq(SysGateLimit::getState, LimitState.USED.name())));
    }

    @Override
    public void refreshLimitCache() {
        cacheService.del(RedisKeyConstant.KEY_RATE_LIMIT);
        List<GateLimitDto> limits = listUsed();

        if (Checker.beNotEmpty(limits)) {
            for (GateLimitDto limit : limits) {
//                GateLimitDto limit = limits.get(0);
                String id = limit.getId();
                limit.setRequests(gateLimitRequestService.listByLimit(id));
                limit.setUsers(gateLimitUserService.listByLimit(id));
                limit.setUtags(gateLimitUtagService.listByLimit(id));
                limit.setStags(gateLimitStagService.listByLimit(id));
                limit.setTusers(gateLimitTuserService.listByLimit(id));
                limit.setUuid(SnowflakeIdWorker.getId());
            }
            cacheService.set(RedisKeyConstant.KEY_RATE_LIMIT, JSONArray.toJSONString(limits));
        }
    }
}
