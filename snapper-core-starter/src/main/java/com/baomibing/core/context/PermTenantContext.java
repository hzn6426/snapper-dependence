
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

package com.baomibing.core.context;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.baomibing.tool.user.UserContext;
import com.baomibing.tool.util.Checker;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import java.util.List;
import java.util.Set;

/**
 * PermContext 用于忽略某个Action权限(引用某个权限方法前调用)
 *
 * @author zening 2022/2/11 08:53
 * @version 1.0.0
 */
public class PermTenantContext {

    //=======================================================================//
    //               ignore Company Scope Dynamic                            //
    //=======================================================================//

    private static final TransmittableThreadLocal<Boolean> ignoreCompanyLocal = new TransmittableThreadLocal<>();

    //设置只过滤公司组织
    public static void withIgnoreCompanyScope(Boolean yes) {
        ignoreCompanyLocal.set(yes);
    }

    public static Boolean beIgnoreCompanyScope() {
        Boolean yes = ignoreCompanyLocal.get();
        return !Checker.beNull(yes) && yes;
    }

    public static void removeIgnoreCompanyScope() {
        ignoreCompanyLocal.remove();
    }

    //=======================================================================//
    //                only filter Company Dynamic                            //
    //=======================================================================//

    private static final TransmittableThreadLocal<Boolean> onlyCompanyLocal = new TransmittableThreadLocal<>();

    //设置只过滤公司组织
    public static void withOnlyFilterCompany(Boolean yes) {
        onlyCompanyLocal.set(yes);
    }

    public static Boolean beOnlyFilterCompany() {
        Boolean yes = onlyCompanyLocal.get();
        return !Checker.beNull(yes) && yes;
    }

    public static void removeOnlyFilterCompany() {
        onlyCompanyLocal.remove();
    }


    //=======================================================================//
    //                ignore User column Dynamic                             //
    //=======================================================================//
    private static final TransmittableThreadLocal<Boolean> ignoreCreateUserColumnLocal = new TransmittableThreadLocal<>();

    public static void withIgnoreCreateUserColumn(Boolean yes) {
        ignoreCreateUserColumnLocal.set(yes);
    }

    public static Boolean beIgnoreCreateUserColumn() {
        Boolean yes = ignoreCreateUserColumnLocal.get();
        return !Checker.beNull(yes) && yes;
    }

    public static void removeIgnoreCreateUserColumn() {
        ignoreCreateUserColumnLocal.set(null);
    }

    //=======================================================================//
    //                 ignore User scope Dynamic                             //
    //=======================================================================//
    private static final TransmittableThreadLocal<Boolean> ignoreUserScopeLocal = new TransmittableThreadLocal<>();

    public static void withIgnoreUserScope(Boolean yes) {
        ignoreGroupScopeLocal.set(yes);
    }

    public static Boolean beIgnoreUserScope() {
        Boolean yes = ignoreGroupScopeLocal.get();
        return !Checker.beNull(yes) && yes;
    }

    public static void removeIgnoreUserScope() {
        ignoreGroupScopeLocal.set(null);
    }

    //=======================================================================//
    //                 ignore group scope Dynamic                            //
    //=======================================================================//
    private static final TransmittableThreadLocal<Boolean> ignoreGroupScopeLocal = new TransmittableThreadLocal<>();

    public static void withIgnoreGroupScope(Boolean yes) {
        ignoreGroupScopeLocal.set(yes);
    }

    public static Boolean beIgnoreGroupScope() {
        Boolean yes = ignoreGroupScopeLocal.get();
        return !Checker.beNull(yes) && yes;
    }

    public static void removeIgnoreGroupScope() {
        ignoreGroupScopeLocal.set(null);
    }

    //=======================================================================//
    //                  add customer User dynamic                            //
    //=======================================================================//
    private static final TransmittableThreadLocal<Set<String>> userNoLocal = new TransmittableThreadLocal<>();
    //添加指定的用户 用来查看不仅仅是当前用户,还有指定的用户
    public static void addUserNos(Set<String> userNos) {
        if (!hasUserNos()) {
            String currentUserName = UserContext.currentUserName();
            if (Checker.beNotEmpty(currentUserName)) {
                userNos.add(currentUserName);
            }
        }
        userNoLocal.set(userNos);
    }

    public static boolean hasUserNos() {
        Set<String> userNos = userNoLocal.get();
        return Checker.beNotEmpty(userNos);
    }

    public static List<String> listUserNos() {
        return hasUserNos() ? Lists.newArrayList(userNoLocal.get()) : Lists.newArrayList();
    }

    public static void removeUserNos() {
        if (hasUserNos()) {
            Set<String> sets = userNoLocal.get();
            sets.clear();
        }
    }

    //=======================================================================//
    //                      Ignore Action Dynamic                            //
    //=======================================================================//

    private static final TransmittableThreadLocal<Set<String>> permLocal = new TransmittableThreadLocal<>();

    public static void ignoreAction(String action) {
        if (Checker.beEmpty(action)) return;
        Set<String> ignores = permLocal.get();
        if (Checker.beNull(ignores)) {
            ignores = Sets.newHashSet();
        }
        ignores.add(action);
        permLocal.set(ignores);
    }


    public static boolean hasIgnore(String action) {
        if (Checker.beEmpty(action)) return false;
        Set<String> ignores = permLocal.get();
        if (Checker.beEmpty(ignores)) return false;
        return ignores.stream().anyMatch(i -> i.equals(action));
    }


    public static void remove(String action) {
        if (Checker.beEmpty(action)) return;
        Set<String> ignores = permLocal.get();
        if (Checker.beEmpty(ignores)) return;
        ignores.remove(action);
    }
}
