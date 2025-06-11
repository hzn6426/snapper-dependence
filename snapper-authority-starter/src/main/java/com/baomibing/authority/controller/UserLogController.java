/**
 * Copyright (c) 2018-2023, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.authority.controller;

import com.baomibing.authority.dto.HmacLogDto;
import com.baomibing.authority.dto.UserLogDto;
import com.baomibing.authority.service.SysHmacLogService;
import com.baomibing.authority.service.SysUserLogService;
import com.baomibing.core.common.Assert;
import com.baomibing.core.common.SearchResult;
import com.baomibing.core.wrap.UserLogEventWrap;
import com.baomibing.tool.common.PageQuery;
import com.baomibing.web.base.MBaseController;
import com.baomibing.web.common.R;
import com.github.dozermapper.core.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

/**
 * 用户日志请求
 *
 * @author zening
 * @date Apr 25, 2021 10:25:27 AM
 * @version 1.0.0
 */
@RestController
@RequestMapping(path = {"/api/userLog","/fapi/userLog"}, consumes = {"application/json",
		"application/x-www-form-urlencoded"}, produces = MediaType.APPLICATION_JSON_VALUE)
public class UserLogController extends MBaseController<UserLogDto> {

	@Autowired private SysUserLogService logService;
	@Autowired private Mapper mapper;
	@Autowired private SysHmacLogService hmacLogService;
	
	@PostMapping("/search")
	public R<UserLogDto> search(@RequestBody PageQuery<UserLogDto> pager) {
		SearchResult<UserLogDto> result = logService.search(pager.getDto(), pager.getPageNo(), pager.getPageSize());
		return R.build(result);
	}

	@GetMapping("/{id}")
	public UserLogDto getLog(@PathVariable("id") String id) {
		return logService.getIt(id);
	}

	@PostMapping
	public void saveLogAsync(@RequestBody UserLogEventWrap logEvent) {
		Assert.CheckArgument(logEvent);
		if (logEvent.getBeHmacRequest()) {
			HmacLogDto hmacLog = new HmacLogDto();
			hmacLog.setDataFrom(logEvent.getOuterSystem()).setDataTo("ME").setExchangeName(logEvent.getExchangeName())
					.setExchangeMethod(logEvent.getExchangeMethod()).setExchangeParam(logEvent.getExchangeParam())
					.setExchangeTime(logEvent.getExchangeTime()).setExchangeUrl(logEvent.getExchangeUrl())
					.setExceptionMsg(logEvent.getExceptionMsg()).setIpAddress(logEvent.getIpAddress())
					.setState(logEvent.getState()).setCreateUser(logEvent.getCreateUser())
					.setCreateUserCnName(logEvent.getCreateUserCnName()).setUpdateUserCnName(logEvent.getUpdateUserCnName())
					.setUpdateUser(logEvent.getUpdateUser())
					.setDataContent(logEvent.getResponseData()).setSystemTag(logEvent.getSystemTag());
			hmacLogService.doSaveLogAsync(hmacLog);
		} else {
			UserLogDto userLog = mapper.map(logEvent, UserLogDto.class);
			logService.doSaveLogAsync(userLog);
		}
	}
	
}
