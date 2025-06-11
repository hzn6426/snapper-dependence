
package com.baomibing.authority.service.impl;


import com.baomibing.authority.constant.enums.ResourceTypeEnum;
import com.baomibing.authority.dto.BusinessPermDto;
import com.baomibing.authority.dto.ButtonDto;
import com.baomibing.authority.dto.ResourceApiDto;
import com.baomibing.authority.entity.SysButton;
import com.baomibing.authority.mapper.SysButtonMapper;
import com.baomibing.authority.service.*;
import com.baomibing.authority.state.CommonState;
import com.baomibing.core.common.Assert;
import com.baomibing.core.common.SearchResult;
import com.baomibing.orm.base.MBaseServiceImpl;
import com.baomibing.tool.constant.RedisKeyConstant;
import com.baomibing.tool.constant.Strings;
import com.baomibing.tool.util.Checker;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * 按钮实现类
 *
 * @author zening
 * @since 1.0.0
 */
@Service
public class SysButtonServiceImpl extends MBaseServiceImpl<SysButtonMapper, SysButton, ButtonDto> implements SysButtonService {
    
    @Autowired private SysMenuService menuService;
    @Autowired private SysResourceApiService resourceApiService;
    @Autowired private SysRoleResourceService roleResourceService;
    @Autowired private SysBusinessPermService businessPermService;


    @Override
    public List<ButtonDto> listByMenus(Set<String> menuIds) {
        if (Checker.beEmpty(menuIds)) {
            return Lists.newArrayList();
        }
        return mapper(this.baseMapper.selectList(lambdaQuery().in(SysButton::getMenuId, menuIds)));
    }


    @Override
    public SearchResult<ButtonDto> searchButtonsAndApiByMenu(ButtonDto v, int pageNumber, int pageSize) {
        if (Checker.beNull(v)) {
            return new SearchResult<>(0, Lists.newArrayList());
        }
        int offset = offset(pageNumber,pageSize);
        List<ButtonDto> buttons = mapper(baseMapper.listButtonAndApiByMenu(v.getMenuId(), v.getKeyword(),  pageSize, offset));
        int count = baseMapper.countButtonAndApiByMenu(v.getMenuId(), v.getKeyword());
        return new SearchResult<>(count, buttons);
    }

    private void saveButton(ButtonDto button) {
        Assert.CheckArgument(button);
        Assert.CheckArgument(button.getMenuId());
        ResourceApiDto resource = resourceApiService.getByResouce(button.getId(), ResourceTypeEnum.BUTTON);
        boolean beNotExist = resource == null;
        if (beNotExist) {
            resource = new ResourceApiDto();
        }
        resource.setResourceId(button.getId()).setResourceType(ResourceTypeEnum.BUTTON.name()).setState(CommonState.ACTIVE)
                .setReqUrl(button.getReqUrl()).setReqMethod(button.getReqMethod());
        if (beNotExist) {
            resourceApiService.saveIt(resource);
        } else {
            resourceApiService.updateIt(resource);
        }
        super.saveIt(button);
    }

    private void updateButton(ButtonDto button) {
        Assert.CheckArgument(button);
        ResourceApiDto resource = resourceApiService.getByResouce(button.getId(), ResourceTypeEnum.BUTTON);
        boolean beNotExist = resource == null;
        if (beNotExist) {
            resource = new ResourceApiDto();
        }
        resource.setResourceId(button.getId()).setResourceType(ResourceTypeEnum.BUTTON.name()).setState(CommonState.ACTIVE)
                .setReqUrl(button.getReqUrl()).setReqMethod(button.getReqMethod());
        if (beNotExist) {
            resourceApiService.saveIt(resource);
        } else {
            resourceApiService.updateIt(resource);
        }
        super.updateIt(button);
    }

    @Override
    public void saveOrUpdateButton(ButtonDto button) {
        Assert.CheckArgument(button);
        ButtonDto dbButton = super.getIt(button.getId());
        if (Checker.beNull(dbButton)) {
            saveButton(button);
        } else {
            assertBeLock(dbButton);
            updateButton(button);
        }
        if (Checker.beNotEmpty(button.getPermAction())) {
            //保存或更新Perm
            BusinessPermDto perm = new BusinessPermDto();
            perm.setId(button.getPermId()).setButtonId(button.getId()).setMenuId(button.getMenuId())
                    .setReqUrl(button.getReqUrl()).setReqMethod(button.getReqMethod()).setPermName(button.getButtonName())
                    .setPermAction(button.getPermAction());
            businessPermService.saveOrUpdatePerm(perm);
        }
        roleResourceService.refreshPrivileges(button.getId());
    }

    @Override
    public void deleteButtons(Set<String> ids) {
        Assert.CheckArgument(ids);
        List<ButtonDto> buttons = super.gets(ids);
        for (ButtonDto b : buttons) {
            assertBeLock(b);
        }
        List<ResourceApiDto> resources = resourceApiService.listByResource(ids, ResourceTypeEnum.BUTTON);
        if (Checker.beNotEmpty(resources)) {
            resourceApiService.deleteItBatch(resources);
            for (ResourceApiDto a : resources) {
                cacheService.del(RedisKeyConstant.CACHE_API_PREFIX + a.getReqMethod() + Strings.DOUBLE_AT + a.getReqUrl());
            }
        }
        roleResourceService.deleteByResources(ids, ResourceTypeEnum.BUTTON);
        businessPermService.deleteByButtons(ids);
        super.deletes(ids);
    }

    @Override
    public List<ButtonDto> listByKeyWord(String keyWord) {
        if (Checker.beEmpty(keyWord)) {
            return Lists.newArrayList();
        }
        return mapper(baseMapper.listButtonByKeyWork(keyWord));
    }

    @Override
    public ButtonDto getButtonAndApiById(String id) {
        if (Checker.beEmpty(id)) {
            return null;
        }
        SysButton button = baseMapper.getButtonAndApiById(id);
        return Checker.beNull(button) ? null : mapper2v(button);
    }
    
}
