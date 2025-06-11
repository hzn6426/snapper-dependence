/**
 * Copyright (c) 2018-2023, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.tool.user;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.baomibing.tool.util.Checker;
import com.google.common.base.Joiner;

import java.util.Optional;

/**
 * 用户上下文
 * @author zening
 * @version 1.0.0
 */
public class UserContext {
	private static final TransmittableThreadLocal<User> locale = new TransmittableThreadLocal<>();
	
	public static void putUser(User user) {
		locale.set(user);
	}

	public static Optional<User> currentUser() {
		User u = locale.get();
		if ((Checker.beNotNull(u)) && (u instanceof TenantUser)) {
			return Optional.of((TenantUser) locale.get());
		} else if (Checker.beNotNull(u)) {
			return Optional.of(u);
		}
		return Optional.empty();
	}

	public static Optional<TenantUser> currentTenantUser() {
		User u = locale.get();
		if ((Checker.beNotNull(u)) && (u instanceof TenantUser)) {
			return Optional.of((TenantUser) u);
		}
		return Optional.empty();
	}
	
	public static boolean exist() {
		return currentUser().isPresent();
	}

	public static void remove() {
		locale.remove();
	}

	public static String currentTenantId() {
		return currentTenantUser().map(TenantUser::getTenantId).orElse(null);
	}

	public static String currentUserName() {
		return currentUser().map(User::getUserName).orElse(null);
	}

	public static String currentUserCnName() {
		return currentUser().map(User::getUserCnName).orElse(null);
	}

	public static String currentUserEnName() {
		return currentUser().map(User::getUserEnName).orElse(null);
	}

	public static String currentGateWayTag() {
		return currentUser().map(User::getGateWayTag).orElse(null);
	}

	public static String currentUserToken() {
		return currentUser().map(User::getToken).orElse(null);
	}

	public static String currentUserId() {
		return currentUser().map(User::getId).orElse(null);
	}

	public static String currentUserGroupId() {
		return currentUser().map(User::getCurrentGroupId).orElse(null);
	}

	public static String currentUserPositionId() {
		return currentUser().map(User::getCurrentPositionId).orElse(null);
	}

	public static String currentUserRoles() {
		return currentUser().map(u -> Checker.beNotEmpty(u.getRoles()) ? Joiner.on(",").join(u.getRoles()) : "").orElse(null);
	}

	public static String currentUserTag() {
		return currentUser().map(User::getUserTag).orElse(null);
	}

	public static String currentCompanyId() {
		return currentUser().map(User::getCompanyId).orElse(null);
	}

	public static String currentCompanyName() {
		return currentUser().map(User::getCompanyName).orElse(null);
	}

	public static String outerSystemName() {
		return currentUser().map(User::getOuterSystemName).orElse(null);
	}

	public static String currentSystemTag() {
		return currentUser().map(User::getSystemTag).orElse(null);
	}

	public static String hmacUserGroupName() {
		return currentUser().map(User::getHmacGroupName).orElse(null);
	}

	public static String hmacUserName() {
		return currentUser().map(User::getHmacUserName).orElse(null);
	}

	public static String hmacUserGroupId() {
		return currentUser().map(User::getHmacGroupId).orElse(null);
	}

	public static String hmacUserCnName() {
		return currentUser().map(User::getHmacUserCnName).orElse(null);
	}

	public static String currentOuterSystemName() {
		return currentUser().map(User::getOuterSystemName).orElse(null);
	}
}
