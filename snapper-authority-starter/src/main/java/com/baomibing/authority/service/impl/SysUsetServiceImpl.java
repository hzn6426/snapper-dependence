
package com.baomibing.authority.service.impl;

import com.baomibing.authority.action.UsetAction;
import com.baomibing.authority.constant.PermActionConst;
import com.baomibing.authority.constant.PermConnectConst;
import com.baomibing.authority.dto.UsetDto;
import com.baomibing.authority.entity.SysUset;
import com.baomibing.authority.mapper.SysUsetMapper;
import com.baomibing.authority.service.SysUserUsetService;
import com.baomibing.authority.service.SysUsetRoleService;
import com.baomibing.authority.service.SysUsetService;
import com.baomibing.core.annotation.Action;
import com.baomibing.core.annotation.ActionConnect;
import com.baomibing.core.common.Assert;
import com.baomibing.core.common.SearchResult;
import com.baomibing.core.exception.ExceptionEnum;
import com.baomibing.core.exception.ServerRuntimeException;
import com.baomibing.core.process.StateWorkFlow;
import com.baomibing.core.wrap.CommonTreeWrap;
import com.baomibing.orm.base.MBaseServiceImpl;
import com.baomibing.tool.util.Checker;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

/**
 * 用户组服务实现
 *
 * @author zening
 * @version 1.0.0
 */
@Service
public class SysUsetServiceImpl extends MBaseServiceImpl<SysUsetMapper, SysUset, UsetDto> implements SysUsetService {

    @Autowired private SysUserUsetService userUsetService;
    @Autowired private SysUsetRoleService usetRoleService;
    
    @Action(value = PermActionConst.USET_SEARCH)
    @ActionConnect(value = {PermConnectConst.SELECT_LIST, PermConnectConst.SELECT_COUNT})
    @Override
    public SearchResult<UsetDto> search(UsetDto usetDto, int pageNumber, int pageSize) {
		LambdaQueryWrapper<SysUset> wrapper = lambdaQuery().like(Checker.beNotEmpty(usetDto.getUsetName()), SysUset::getUsetName, usetDto.getUsetName())
				.eq(Checker.beNotEmpty(usetDto.getState()), SysUset::getState, usetDto.getState());
		return super.search(wrapper, pageNumber, pageSize);
    }
    

    @Override
    public void saveUset(UsetDto uset) {
//        throw new ServerRuntimeException(AuthorizationExceptionEnum.THE_VERSION_CAN_NOT_HAVE_THIS_FUNCTION);
        Assert.CheckArgument(uset);
        StateWorkFlow.doInitState(uset);
        super.saveIt(uset);
    }

    @Override
    public void updateUset(UsetDto uset) {
//        throw new ServerRuntimeException(AuthorizationExceptionEnum.THE_VERSION_CAN_NOT_HAVE_THIS_FUNCTION);
        UsetDto dbUset = super.getIt(uset.getId());
        assertBeLock(dbUset);
        super.updateIt(uset);
    }

    private void updateStateByIds(Set<String> ids, UsetAction action) {
        Assert.CheckArgument(ids);
        Assert.CheckArgument(action);
        List<UsetDto> usets = super.gets(ids);
        if (usets.size() != ids.size()) {
            throw new ServerRuntimeException(ExceptionEnum.IDS_CONTAINS_INVALID_ID);
        }
        for(UsetDto uset : usets) {
            assertBeLock(uset);
            StateWorkFlow.doProcess(uset, action);
        }
        super.updateItBatch(usets);
    }
    
    
    @Transactional
    @Override
    public void use(Set<String> ids) {
        updateStateByIds(ids, UsetAction.USE);
    }
    
    @Transactional
    @Override
    public void stop(Set<String> ids) {
        updateStateByIds(ids, UsetAction.STOP);
    }
    
    @Transactional
    @Override
    public void deleteUsets(Set<String> ids) {
//        throw new ServerRuntimeException(AuthorizationExceptionEnum.THE_VERSION_CAN_NOT_HAVE_THIS_FUNCTION);
        Assert.CheckArgument(ids);
        List<UsetDto> usets = super.gets(ids);
        for (UsetDto r : usets) {
            assertBeLock(r);
            StateWorkFlow.doAssertDelete(r);
        }
        userUsetService.deleteByUsets(ids);
        usetRoleService.deleteByUsets(ids);
        super.deleteItBatch(usets);
    }

    @Action(value = PermActionConst.USET_TREE_ALL_USET)
    @ActionConnect(value = {PermConnectConst.SELECT_LIST})
	@Override
	public List<CommonTreeWrap> treeAllUset() {
        List<CommonTreeWrap> trees = Lists.newArrayList();
		List<UsetDto> usets = mapper(baseMapper.selectList(lambdaQuery()));
        if (Checker.beEmpty(usets)) {
            return trees;
        }
        usets.forEach(u -> trees.add(new CommonTreeWrap().setKey(u.getId()).setTitle(u.getUsetName()).setIsLeaf(Boolean.TRUE)));
        return trees;
	}


}
