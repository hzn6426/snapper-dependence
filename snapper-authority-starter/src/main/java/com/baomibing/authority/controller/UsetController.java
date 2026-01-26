
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

import com.baomibing.authority.dto.UsetDto;
import com.baomibing.authority.service.SysUsetService;
import com.baomibing.core.common.SearchResult;
import com.baomibing.core.wrap.CommonTreeWrap;
import com.baomibing.tool.common.PageQuery;
import com.baomibing.web.annotation.ULog;
import com.baomibing.web.base.MBaseController;
import com.baomibing.web.common.R;
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
@RequestMapping(path = "/api/uset", consumes = {"application/json", "application/x-www-form-urlencoded"}, produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
public class UsetController extends MBaseController<UsetDto> {
	@Autowired private SysUsetService usetService;
    
    /**
     * 根据分页条件查询角色列表
     *
     * @param pageQuery 分页查询条件
     * @return
     */
    @ULog("用户组查询")
    @PostMapping("/search")
    public R<UsetDto> search(@RequestBody PageQuery<UsetDto> pageQuery) {
        SearchResult<UsetDto> result = usetService.search(pageQuery.getDto(), pageQuery.getPageNo(), pageQuery.getPageSize());
        return R.build(result.getTotalSize(), pageQuery.getPageNo(), pageQuery.getPageSize(), result.getDataList());
    }
    
    /**
     * 新增用户组
     *
     * @param uset 用户组对象
     */
    @ULog("用户组新增")
    @PostMapping()
    public void save(@RequestBody @Valid UsetDto uset) {
        usetService.saveUset(uset);
    }
    
    /**
     * 根据ID获取用户组信息
     *
     * @param id 用户组ID
     * @return
     */
    @GetMapping("/{id}")
	public UsetDto getUset(@PathVariable("id") String id) {
        return usetService.getIt(id);
    }
    
    /**
     * 更新用户组对象
     *
     * @param uset 用户组
     */
    @ULog("用户组更新")
    @PutMapping
    public void update(@RequestBody @Valid UsetDto uset) {
        usetService.updateUset(uset);
    }
    
    /**
     * 删除用户组列表
     *
     * @param ids 用户组ID列表
     */
    @ULog("用户组删除")
    @DeleteMapping
    public void delete(@RequestBody List<String> ids) {
        usetService.deleteUsets(Sets.newHashSet(ids));
    }
    
    /**
     * 用户组启用
     *
     * @param ids 用户组ID列表
     */
    @ULog("用户组启用")
    @PostMapping("use")
    public void use(@RequestBody List<String> ids) {
        usetService.use(Sets.newHashSet(ids));
    }
    
    /**
     * 用户组停用
     *
     * @param ids 组ID列表
     */
    @ULog("用户组停用")
    @PostMapping("stop")
    public void stop(@RequestBody List<String> ids) {
        usetService.stop(Sets.newHashSet(ids));
    }

    /**
     * 用户组获取所有
     * @return
     */
	@GetMapping("/treeAllUset")
	public List<CommonTreeWrap> treeAllUset() {
		return usetService.treeAllUset();
	}
}
