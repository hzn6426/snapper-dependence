/**
 * Copyright (c) 2018-2025, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.authority.controller;

import com.baomibing.authority.service.SystemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
