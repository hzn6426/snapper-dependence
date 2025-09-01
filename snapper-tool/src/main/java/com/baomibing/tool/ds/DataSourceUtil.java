/**
 * Copyright (c) 2018-2025, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
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
