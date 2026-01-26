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
package com.baomibing.tool.ds;


/**
 * DataSourceUtil
 *
 * @author zening 2022/9/21 10:18
 * @version 1.0.0
 */
public class DataSourceUtil {

//    public static void dispatchDataSources(Set<String> dataSources) {
//        Validate.notEmpty(dataSources, "dataSource not be Null Or Empty!");
//        UserRequest req = RequestContext.currentRequest().orElse(null);
//        if (Checker.beNull(req)) {
//            throw new RuntimeException("User Request Not Exist");
//        }
//        String method = req.getMethod();
//        String url = req.getUrl();
//        String params = req.getParams();
//        String token = UserContext.currentUserToken();
//        List<CompletableFuture<Void>> futures = Lists.newArrayList();
//        for (String ds : dataSources) {
//            futures.add(wrapFuture(ds, url, method, params, token));
//        }
//        futures.forEach(f -> {
//            try {
//                f.join();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        });
//    }
//
//    private static CompletableFuture<Void> wrapFuture(String ds, String url, String method, String params, String token) {
//
//        Set<String> methods = Sets.newHashSet("POST", "GET", "PUT", "DELETE");
//        if (!methods.contains(method)) {
//            throw new IllegalArgumentException("Illegal Argument method, must be one of [POST,PUT,GET,DELETE]!");
//        }
//        return CompletableFuture.supplyAsync(() -> {
//            IHttpClient.Builder builder = IHttpClient.create();
//            Map<String, String> headers = Maps.newHashMap();
//            headers.put(UserHeaderConstant.X_DS, ds);
//            headers.put(UserHeaderConstant.USER_AUTHORIZATION, token);
//            switch (method) {
//                case "POST":
//                    builder.buildPost().sendXRaw(url, headers, params);
//                    break;
//                case "GET":
//                    builder.buildGet().sendXRaw(url, headers);
//                    break;
//                case "DELETE":
//                    builder.buildDelete().sendXRaw(url, headers, params);
//                    break;
//                case "PUT":
//                    builder.buildPut().sendXRaw(url, headers, params);
//                    break;
//            }
//            return null;
//        });
//
//    }
}
