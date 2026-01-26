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

package com.baomibing.authority.controller;

import com.baomibing.authority.dto.SysTenantUsetDto;
import com.baomibing.authority.service.SysTenantUsetService;
import com.baomibing.authority.vo.TenantBatchVo;
import com.baomibing.core.common.SearchResult;
import com.baomibing.core.wrap.CommonTreeWrap;
import com.baomibing.web.base.MBaseController;
import com.baomibing.web.common.R;
import com.baomibing.tool.common.PageQuery;
import com.google.common.collect.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 用户组
 * @author : zening
 * @since: 1.0.0
 */
@RequestMapping(path = "/eapi/uset", consumes = {"application/json", "application/x-www-form-urlencoded"}, produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
public class TenantUsetController extends MBaseController<SysTenantUsetDto> {
	@Autowired
    private SysTenantUsetService usetService;
    
    /**
     * 根据分页条件查询角色列表
     *
     * @param pageQuery 分页查询条件
     * @return
     */
    @PostMapping("/search")
    public R<SysTenantUsetDto> search(@RequestBody PageQuery<SysTenantUsetDto> pageQuery) {
        SearchResult<SysTenantUsetDto> result = usetService.search(pageQuery.getDto(), pageQuery.getPageNo(), pageQuery.getPageSize());
        return R.build(result.getTotalSize(), pageQuery.getPageNo(), pageQuery.getPageSize(), result.getDataList());
    }
    
    /**
     * 新增用户组
     *
     * @param uset 用户组对象
     */
    @PostMapping()
    public void save(@RequestBody @Valid SysTenantUsetDto uset) {
        usetService.doSave(uset);
    }
    
    /**
     * 根据ID获取用户组信息
     *
     * @param id 用户组ID
     * @return
     */
    @GetMapping("/{id}")
	public SysTenantUsetDto getUset(@PathVariable("id") String id) {
        return usetService.getIt(id);
    }
    
    /**
     * 更新用户组对象
     *
     * @param uset 用户组
     */
    @PutMapping
    public void update(@RequestBody @Valid SysTenantUsetDto uset) {
        usetService.doUpdate(uset);
    }
    
    /**
     * 删除用户组列表
     *
     * @param vo 用户组ID列表
     */
    @DeleteMapping
    public void delete(@RequestBody TenantBatchVo vo) {
        usetService.deleteUsets(Sets.newHashSet(vo.getIds()), vo.getTenantId());
    }
    
    /**
     * 用户组启用
     *
     * @param ids 用户组ID列表
     */
    @PostMapping("use")
    public void use(@RequestBody List<String> ids) {
        usetService.use(Sets.newHashSet(ids));
    }
    
    /**
     * 用户组停用
     *
     * @param ids 组ID列表
     */
    @PostMapping("stop")
    public void stop(@RequestBody List<String> ids) {
        usetService.stop(Sets.newHashSet(ids));
    }

    /**
     * 用户组获取所有
     * @return
     */
	@GetMapping("/treeAllUset")
	public List<CommonTreeWrap> treeAllUset(@RequestParam String tid) {
		return usetService.treeAllUset(tid);
	}

}
