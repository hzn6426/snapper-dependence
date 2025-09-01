/**
 * Copyright (c) 2018-2025, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.web.base;


import com.baomibing.tool.user.EmailContext;
import com.baomibing.tool.user.EmailServer;
import com.baomibing.tool.user.User;
import com.baomibing.tool.user.UserContext;
import com.baomibing.tool.util.Checker;
import com.baomibing.tool.util.URLUtil;
import com.google.common.collect.Sets;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Set;

import static com.baomibing.tool.user.UserContext.currentUser;

/**
 * 封装spring mvc通用获取request,response,session等方法
 * 
 * @author zening
 * @since 1.0.0
 */
public class ActionController {
	
	protected HttpServletRequest getRequest() {
		return ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();
	}
	
	protected HttpServletResponse getResponse() {
		return ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getResponse();
	}
	
	protected Object getRequestAttribute(String name) {
		return getRequest().getAttribute(name);
	}
	
	protected Object getSessionAttribute(String name) {
		return getSession().getAttribute(name);
	}
	
	protected HttpSession getSession() {
		return getRequest().getSession();
	}
	
	protected String getRequestParameter(String name) {
		return getRequest().getParameter(name);
	}
	
	protected void setReqAttr(String name, Object value) {
		getRequest().setAttribute(name, value);
	}
	
	protected void setSessionAttr(String name, Object value) {
		getSession().setAttribute(name, value);
	}
	
	protected String getHeader(String header) {
		return getRequest().getHeader(header);
	}
	
	protected String currentUserName() {
		return currentUser().map(u -> u.getUserName()).orElse(null);
	}
	
	protected String currentUserCnName() {
		String cnName =  currentUser().map(u -> u.getUserCnName()).orElse(null);
		return Checker.beEmpty(cnName) ? cnName : URLUtil.decode(URLUtil.decode(cnName));
	}
	
	protected String currentUserId() {
		return currentUser().map(u -> u.getId()).orElse(null);
	}
	
	protected String currentUserGroupId() {
		return currentUser().map(u -> u.getCurrentGroupId()).orElse(null);
	}

	protected String currentUserGroupName() {
		return currentUser().map(u -> u.getCurrentGroupName()).orElse(null);
	}

	protected boolean userNotLogin() {
		return !UserContext.exist();
	}
	
	protected Set<String> currentUserRoles() {
		return currentUser().map(u -> u.getRoles()).orElse(Sets.newHashSet());
	}

	protected EmailServer currentEmailServer() {
		return EmailContext.currentEmailServer().orElse(null);
	}

	protected String currentUserSystemTag() {
		return (String)UserContext.currentUser().map(User::getSystemTag).orElse(null);
	}
}
