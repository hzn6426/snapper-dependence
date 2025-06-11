/**
 * Copyright (c) 2018-2023, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.authority.controller;

import com.baomibing.authority.dto.ParamDto;
import com.baomibing.authority.service.SysParamService;
import com.baomibing.core.common.CollectionMapperDecorator;
import com.baomibing.core.common.SearchResult;
import com.baomibing.tool.common.PageQuery;
import com.baomibing.tool.util.Checker;
import com.baomibing.web.annotation.NotWrap;
import com.baomibing.web.annotation.ULog;
import com.baomibing.web.base.MBaseController;
import com.baomibing.web.common.R;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 参数管理
 *
 * @author : zening
 * @since : 1.0.0
 */

@RestController
@RequestMapping(path = {"/api/param","/fapi/param"}, consumes = {"application/json", "application/x-www-form-urlencoded"}, produces = MediaType.APPLICATION_JSON_VALUE)
public class ParamController extends MBaseController<ParamDto> {

    @Autowired private SysParamService sysParamService;
	@Autowired private CollectionMapperDecorator collectionMapper;

    /**
     * 参数保存
     *
     * @param dto
     * @Return: void
     */
    @ULog("参数保存")
    @PostMapping
	public void saveParam(@RequestBody @Valid ParamDto dto) {
        sysParamService.saveParam(dto);
    }

    /**
     * 参数更新
     *
     * @param dto
     * @Return: void
     */
    @ULog("参数更新")
    @PutMapping
	public void updateParam(@RequestBody @Valid ParamDto dto) {
        sysParamService.updateParam(dto);
    }

    /**
     * 参数查询
     *
     * @param query
     * @Return: com.baomibing.core.common.R<com.baomibing.authority.dto.dto>
     */
    @ULog("参数查询")
    @PostMapping("search")
	public R<ParamDto> search(@RequestBody PageQuery<ParamDto> query) {
		SearchResult<ParamDto> result = sysParamService.searchParam(query.getDto(), query.getPageNo(),
				query.getPageSize());
        return R.build(result);
    }

    /**
     * 批量删除参数
     *
     * @param ids 参数id数组
     * @Return: void
     */
    @ULog("参数批量删除")
    @DeleteMapping
    public void deleteParams(@RequestBody List<String> ids) {
        sysParamService.deleteParams(ids);
    }

    /**
     * 根据编码列表获取对应的参数列表
     *
     * @param codes 编码列表
     * @Return: java.util.List<com.baomibing.authority.dto.ParamDto>
     */
    @NotWrap
    @PostMapping("listByCodes")
    public List<ParamDto> listByCodes(@RequestBody List<String> codes) {
		List<ParamDto> params = sysParamService.listByCodes(codes);
		return Checker.beEmpty(params) ? Lists.newArrayList()
				: Lists.newArrayList(collectionMapper.mapCollection(params, ParamDto.class));

    }
}
