
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

import com.baomibing.authority.exception.AuthorizationExceptionEnum;
import com.baomibing.authority.service.SystemService;
import com.baomibing.core.exception.ServerRuntimeException;
import com.baomibing.tool.constant.RedisKeyConstant;
import com.baomibing.tool.user.RequestContext;
import com.google.common.base.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.text.MessageFormat;
import java.util.concurrent.atomic.AtomicInteger;

@RequestMapping(path = { "/api/oauth" }, produces = MediaType.APPLICATION_JSON_VALUE)
@RestController @Slf4j
public class SystemController extends ASystemController {



	@Autowired
	public SystemController(SystemService systemService) {
		super(systemService);
	}

	@Override
	public boolean validateCaptcha(String captcha) {
		return true;
	}


}
