/**
 * Copyright (c) 2018-2025, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.orm.base;


import com.baomibing.core.common.Assert;
import com.baomibing.tool.user.*;
import com.baomibing.tool.util.Checker;
import com.baomibing.tool.util.URLUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.baomibing.tool.user.UserContext.currentUser;
import static com.baomibing.tool.util.Checker.beEmpty;

/**
 * 上下文服务实现类
 * 
 * @author zening
 * @since 1.0.0
 */
public class ContextServiceImpl {

	protected <E> List<E> emptyList() {
		return Lists.newArrayList();
	}

	protected <E> List<E> emptyList(Collection<E> coll) {
		return Lists.newArrayList(coll);
	}

	protected <E> Set<E> emptySet() {
		return Sets.newHashSet();
	}

	protected <E> Set<E> emptySet(Collection<E> coll) {
		return Sets.newHashSet(coll);
	}

	protected <K,V> Map<K,V> emptyMap() {
		return Maps.newHashMap();
	}

	protected void checkArgument(Collection<?> collection) {
		Assert.CheckArgument(collection);
	}

	protected void checkArgument(String ... references) {
		Assert.CheckArgument(references);
	}

	protected void checkArgument(Object ... references) {
		Assert.CheckArgument(references);
	}

	protected boolean beNotEmpty(Iterable<?> it) {
		return Checker.beNotEmpty(it);
	}

	protected boolean beNotEmpty(CharSequence chars) {
		return Checker.beNotEmpty(chars);
	}

	protected <T> boolean beNotEmpty(T[] arr) {
		return Checker.beNotEmpty(arr);
	}

	protected String currentUserName() {
		return currentUser().map(User::getUserName).orElse(null);
	}
	
	protected String currentUserId() {
		return currentUser().map(User::getId).orElse(null);
	}
	
	protected Set<String> currentUserRoles() {
		return currentUser().map(User::getRoles).orElse(Sets.newHashSet());
	}
	
	protected String currentUserGroupId() {
		return currentUser().map(User::getCurrentGroupId).orElse(null);
	}
	
	protected String currentUserCnName() {
		String cnName =  currentUser().map(User::getUserCnName).orElse(null);
		return beEmpty(cnName) ? cnName : URLUtil.decode(URLUtil.decode(cnName));
	}

	protected String currentTenantId() {
		return UserContext.currentTenantId();
	}
	
	protected String currentUserEnName() {
		return currentUser().map(User::getUserEnName).orElse(null);
	}
	
	protected boolean userNotLogin() {
		return !UserContext.exist();
	}

	protected EmailServer currentEmailServer() {
		return EmailContext.currentEmailServer().orElse(null);
	}

	protected String currentUserTag() {
		return currentUser().map(User::getUserTag).orElse(null);
	}

	protected String currentUserSystemTag() {
		return currentUser().map(User::getSystemTag).orElse(null);
	}

	protected String currentUserToken() {
		return currentUser().map(User::getToken).orElse(null);
	}

	protected String currentReqUrl() {
		return RequestContext.currentRequest().map(UserRequest::getUrl).orElse(null);
	}

	protected String currentReqFullUrl() {
		return RequestContext.currentRequest().map(UserRequest::getFullUrl).orElse(null);
	}

	protected String currentReqIp() {
		return RequestContext.currentRequest().map(UserRequest::getIp).orElse(null);
	}

	protected String currentOs() {
		return RequestContext.currentRequest().map(UserRequest::getOs).orElse(null);
	}

	protected String currentBrowser() {
		return RequestContext.currentRequest().map(UserRequest::getBrowser).orElse(null);
	}
}
