/**
 * Copyright (c) 2018-2025, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.web.interceptor;

import cn.hutool.core.net.URLDecoder;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomibing.cache.CacheService;
import com.baomibing.tool.constant.RedisKeyConstant;
import com.baomibing.tool.constant.Strings;
import com.baomibing.tool.constant.UserHeaderConstant;
import com.baomibing.tool.constant.WebConstant;
import com.baomibing.tool.ds.DataSource;
import com.baomibing.tool.ds.DataSourceContext;
import com.baomibing.tool.user.*;
import com.baomibing.tool.util.Checker;
import com.baomibing.tool.util.JSONUtil;
import com.baomibing.tool.util.ObjectUtil;
import com.baomibing.tool.util.URLUtil;
import com.baomibing.web.annotation.ULog;
import com.baomibing.web.common.MultiReadHttpServletResponse;
import com.baomibing.web.common.WebHelper;
import com.baomibing.web.enums.MethodNameLogRuleEnum;
import com.baomibing.web.enums.UserLogTypeEnum;
import com.baomibing.web.event.TenantUserLogEvent;
import com.baomibing.web.event.UserLogEvent;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import eu.bitwalker.useragentutils.UserAgent;
import io.vavr.collection.LinkedHashSet;
import org.apache.commons.lang3.RandomUtils;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.lang.NonNull;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

import static com.baomibing.tool.constant.RedisKeyConstant.KEY_USER_GATE_WAY_ID;

/**
 * 拦截器，用于拦截请求填充上下文及写日志
 * @author zening
 * @version 1.0.0
 */
public class ContextHandlerInterceptor implements HandlerInterceptor {

    @Autowired private CacheService cacheService;
    @Autowired private ApplicationEventPublisher publisher;
    private static final ThreadLocal<UserLogEvent> localLog = new ThreadLocal<>();

    @Value("${spring.application.name}")
    private String serviceName;
    @Value("${baomibing.ulog:true}")
    private Boolean beLogger = true;

    private static final List<String> whites = Lists.newArrayList(WebConstant.API_USER_LOG_URL, WebConstant.FAPI_USER_LOG_URL,
            WebConstant.TENANT_API_USER_LOG_URL, WebConstant.TENANT_API_REGISTER_URL);

    private static final AntPathMatcher pathMatch = new AntPathMatcher();

    private boolean matchWhiteList(String url) {
        return whites.stream().anyMatch(w -> pathMatch.match(w, url));
    }

    private final Set<String> names = EnumSet.allOf(MethodNameLogRuleEnum.class).stream().map(Enum::name).collect(Collectors.toSet());

    //如果没有ULOG标记将忽略记录文档
    private boolean ignoreULog(HttpServletRequest request, Object handler) {
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();
        ULog ulog = method.getAnnotation(ULog.class);
        return !Checker.beNull(ulog) && (!Checker.beNotNull(request.getContentType()) || request.getContentType().toUpperCase().contains("APPLICATION/JSON"));
    }

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler)  {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        String url = request.getRequestURI();
        if (url.contains(WebConstant.SUFFIX_HTML) || matchWhiteList(url)) {
            return true;
        }

        if (beLogger && ignoreULog(request, handler)) {
            beforeExecuteLog(request, handler);
        }

        buildContext(request);
        return true;
    }


    @Override
    public void afterCompletion(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler, Exception ex)  {
        try {
            if (beLogger && ignoreULog(request, handler)) {
                afterExecuteLog(request, response, ex);
            }
        } finally {
            cleanContext();
        }

    }

    private void buildContext(HttpServletRequest req) {
        String userName = req.getHeader(UserHeaderConstant.USER_NAME);
        //构建RequestContext
        UserRequest ur = new UserRequest();
        ur.setIp(req.getHeader(UserHeaderConstant.USER_IP)).setMethod(req.getMethod())
                .setFullUrl(req.getHeader(UserHeaderConstant.USER_URL)).setUrl(req.getRequestURI())
                .setLanguage(req.getHeader(UserHeaderConstant.LANG));;
        RequestContext.putRequest(ur);
        //构建UserContext
        User user = new User();
        String decodeCnName = URLDecoder.decode(URLDecoder.decode(req.getHeader(UserHeaderConstant.USER_CN_NAME), StandardCharsets.UTF_8), StandardCharsets.UTF_8);
        String outerSystemName = Checker.beEmpty(req.getHeader(UserHeaderConstant.USER_OUTER_SYSTEM)) ? ""
                : URLDecoder.decode(URLDecoder.decode(req.getHeader(UserHeaderConstant.USER_OUTER_SYSTEM), StandardCharsets.UTF_8), StandardCharsets.UTF_8);
        user.setCurrentGroupId(req.getHeader(UserHeaderConstant.USER_GROUP))
                .setCurrentGroupName(URLDecoder.decode(URLDecoder.decode(req.getHeader(UserHeaderConstant.USER_GROUP_NAME), StandardCharsets.UTF_8),StandardCharsets.UTF_8))
                .setId(req.getHeader(UserHeaderConstant.USER_ID))
                .setUserName(req.getHeader(UserHeaderConstant.USER_NAME))
                .setCurrentPositionId(req.getHeader(UserHeaderConstant.USER_POSITION))
                .setUserCnName(decodeCnName)
                .setUserEnName(req.getHeader(UserHeaderConstant.USER_EN_NAME))
                .setCompanyId(req.getHeader(UserHeaderConstant.USER_COMPANY_ID))
                .setCompanyName(URLDecoder.decode(URLDecoder.decode(req.getHeader(UserHeaderConstant.USER_COMPANY_NAME), StandardCharsets.UTF_8), StandardCharsets.UTF_8))
                .setGateWayTag(req.getHeader(UserHeaderConstant.GATE_WAY_REDIRECT_TAG))
                .setUserTag(req.getHeader(UserHeaderConstant.USER_TAG))
                .setParams(req.getHeader(UserHeaderConstant.USER_PARAM))
                .setToken(req.getHeader(UserHeaderConstant.USER_TOKEN))
                .setSystemTag(req.getHeader(UserHeaderConstant.USER_SYSTEM_TAG))
                .setOuterSystemName(outerSystemName)
                .setHmacGroupId(req.getHeader(UserHeaderConstant.HMAC_USER_GROUP))
                .setHmacUserCnName(URLDecoder.decode(URLDecoder.decode(req.getHeader(UserHeaderConstant.HMAC_USER_CN_NAME), StandardCharsets.UTF_8), StandardCharsets.UTF_8))
                .setHmacUserName(req.getHeader(UserHeaderConstant.HMAC_USER_NAME))
                .setHmacGroupName(URLDecoder.decode(URLDecoder.decode(req.getHeader(UserHeaderConstant.HMAC_USER_GROUP_NAME), StandardCharsets.UTF_8), StandardCharsets.UTF_8));

        if (Checker.beNotEmpty(req.getHeader(UserHeaderConstant.USER_ROLES))) {
            user.setRoles(Sets.newHashSet(Splitter.on(Strings.COMMA).splitToList(req.getHeader(UserHeaderConstant.USER_ROLES))));
        }
        String cacheHashKey = (user.getUserName() + Strings.HASH + user.getSystemTag()).toLowerCase();
        String email = ObjectUtil.toStringIfNotNull(cacheService.hGet(MessageFormat.format(RedisKeyConstant.KEY_USER_CONTEXT, cacheHashKey), RedisKeyConstant.KEY_USER_EMAIL));
        if (Checker.beNotEmpty(email)) {
            EmailContext.putEmailServer(JSON.parseObject(email, EmailServer.class));
        }
        //微服务调用 缓存数据
        if (Checker.beNotEmpty(user.getGateWayTag())) {
            long timeout = RandomUtils.nextInt(2, 5);
            timeout = timeout * 1000 * 60;
            cacheService.set(MessageFormat.format(KEY_USER_GATE_WAY_ID, user.getGateWayTag()), JSONObject.toJSONString(user), timeout);
        }
        UserContext.putUser(user);

        //构建数据源
        String ds = req.getHeader(UserHeaderConstant.USER_DS);
        if (Checker.beNotEmpty(ds)) {
            DataSourceContext.putDataSources(new DataSource().setName(ds));
        }
    }

    private UserAgent parseUserAgent(String userAgent) {
        return UserAgent.parseUserAgentString(userAgent);

    }

    //记录日志前置处理
    private  void beforeExecuteLog(HttpServletRequest request, Object handler) {
        String url = request.getRequestURI();
        Date time = new Date();
        String httpMethod = request.getMethod();
        String ip = request.getHeader(UserHeaderConstant.USER_IP);
        String uname = request.getHeader(UserHeaderConstant.USER_NAME);
        String cnName = URLUtil.decode(URLUtil.decode(request.getHeader(UserHeaderConstant.USER_CN_NAME)));
        String systemTag = request.getHeader(UserHeaderConstant.USER_SYSTEM_TAG);
        String flag = request.getHeader(UserHeaderConstant.USER_FLAG);
        String tenantId = request.getHeader(UserHeaderConstant.USER_TENANT_ID);
        if (Checker.beNotEmpty(systemTag) && systemTag.contains(Strings.HASH)) {
            systemTag = systemTag.split(Strings.HASH)[0];
        }
        if (url.startsWith(WebConstant.HMAC_API_LOG_URL) || url.contains(WebConstant.HMAC_API_LOG_URL)) {
            uname = request.getHeader(UserHeaderConstant.HMAC_USER_NAME);
            cnName = URLUtil.decode(URLUtil.decode(request.getHeader(UserHeaderConstant.HMAC_USER_CN_NAME)));
            uname = uname + "(" + URLUtil.decode(URLUtil.decode(request.getHeader(UserHeaderConstant.USER_OUTER_SYSTEM))) + ")";
        }
        //创建Log
        UserLogEvent userLogEvent = new UserLogEvent(serviceName);
        if (Checker.beNotEmpty(flag) && Strings.TENANT.equalsIgnoreCase(flag)) {
            userLogEvent = new TenantUserLogEvent(serviceName);
            ((TenantUserLogEvent)userLogEvent).setTenantId(tenantId);
        }
        userLogEvent.setExchangeUrl(request.getRequestURI())
                .setExchangeMethod(httpMethod).setExchangeTime(time).setIpAddress(ip)
                .setCreateUser(uname).setCreateUserCnName(cnName).setUpdateUser(uname)
                .setUpdateUserCnName(cnName).setSystemTag(systemTag);
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        String controllerName = handlerMethod.getBeanType().getSimpleName();
        Method method = handlerMethod.getMethod();
        String methodRule = LinkedHashSet.ofAll(names).find(n -> method.getName().startsWith(n)).getOrElse(MethodNameLogRuleEnum.other.name());
        String logType = UserLogTypeEnum.valueOf(MethodNameLogRuleEnum.valueOf(methodRule).getLogType()).getSvalue();

        userLogEvent.setExecuteMethod(controllerName + Strings.DOT + method.getName()).setLogTypeCode(logType);

        if (request.getRequestURI().startsWith(WebConstant.HMAC_API_LOG_URL)) {
            userLogEvent.setBeHmacRequest(Boolean.TRUE)
                    .setOuterSystem(URLUtil.decode(URLUtil.decode(request.getHeader(UserHeaderConstant.USER_OUTER_SYSTEM))));
        }
        ULog ulog = method.getAnnotation(ULog.class);

        if (Checker.beNotNull(ulog)) {
            userLogEvent.setExchangeName(ulog.value());
        }
        //解析浏览器信息
        UserAgent cap = parseUserAgent(request.getHeader("User-Agent"));
        userLogEvent.setBrowser(io.vavr.collection.List.of(cap.getBrowser(), cap.getBrowserVersion()).mkString(":"))
                .setOs(io.vavr.collection.List.of(cap.getOperatingSystem().getName(), cap.getOperatingSystem().getDeviceType().getName()).mkString(":"));

//        if(request.getContentType().toUpperCase().contains("APPLICATION/JSON")) {
        if ("post".equalsIgnoreCase(httpMethod) || "put".equalsIgnoreCase(httpMethod) || "delete".equalsIgnoreCase(httpMethod)) {
            String params = WebHelper.receivePost(request);
            if (request.getRequestURI().contains("/oauth/token")) {
                JSONObject json = JSONObject.parseObject(params);
                if (Checker.beNotNull(json)) {
                    String userName = json.getString("userName");
                    userLogEvent.setCreateUser(userName).setCreateUserCnName(userName).setUpdateUser(userName).setUpdateUserCnName(cnName);
                }
            }
            if (Checker.beNotEmpty(params)) {
                userLogEvent.setExchangeParam(params.substring(0, Math.min(params.length(), 4995)));
            }
        } else {
            Map<String, String> paramsMap = new HashMap<>();
            Enumeration<String> paramNameEnum = request.getParameterNames();
            while (Checker.beNotNull(paramNameEnum) && paramNameEnum.hasMoreElements()) {
                String paramName = paramNameEnum.nextElement();
                paramsMap.put(paramName, request.getParameter(paramName));
            }
            if (Checker.beNotEmpty(paramsMap)) {
                String params = JSON.toJSONString(paramsMap);
                userLogEvent.setExchangeParam(params.substring(0, Math.min(params.length(), 2000)));
            }
        }
//        }
        localLog.set(userLogEvent);
    }

    //记录日志后置处理
    private void afterExecuteLog(HttpServletRequest request, HttpServletResponse response, Exception ex) {
        UserLogEvent userLogEvent = localLog.get();
        localLog.remove();
        if (Checker.beNotNull(userLogEvent)) {
            Date time = userLogEvent.getExchangeTime();
            Period period = new Period(new DateTime(time), new DateTime());
            userLogEvent.setExecuteTimer(period.getMillis());
            if (Checker.beNotNull(ex) && Checker.beNotEmpty(ex.getMessage())) {
                String exMessage = ObjectUtil.defaultIfNull(ex.getMessage(), "");
                userLogEvent.setState(Strings.FAILURE).setExceptionMsg(exMessage.substring(0, Math.min(500, exMessage.length())));
            } else {
                String contentType = request.getContentType().toLowerCase();
                if (!request.getRequestURI().toLowerCase().contains("download")
                        && !contentType.contains("multipart")) {
                    String result = "";
                    if (response instanceof MultiReadHttpServletResponse) {
                        MultiReadHttpServletResponse mres = (MultiReadHttpServletResponse) response;
                        result = mres.getResponseAsString();
                    } else {
                        MultiReadHttpServletResponse mres = WebUtils.getNativeResponse(response, MultiReadHttpServletResponse.class);
                        result = mres.getResponseAsString();
                    }
                    if (JSONUtil.isJson(result)) {
                        JSONObject json = JSON.parseObject(result);
                        if (Checker.beNotNull(json) && json.containsKey(Strings.CODE) && json.getInteger(Strings.CODE) != 200) {
                            userLogEvent.setState(Strings.FAILURE);
                            String exMessage = json.toJSONString();
                            userLogEvent.setExceptionMsg(exMessage.substring(0, Math.min(500, exMessage.length())));
                        } else {
                            userLogEvent.setResponseData(json.toJSONString().substring(0, Math.min(500, json.toJSONString().length())));
                            userLogEvent.setState(Strings.SUCCESS);
                        }
                    } else {
                        userLogEvent.setState(Strings.SUCCESS);
                    }
                }
            }
            //需要添加request上下文，否则异步接受事件时，上下文将丢失
            RequestContextHolder.setRequestAttributes(RequestContextHolder.getRequestAttributes(), true);
            //发布事件，如果实现了该事件的监听器则进行日志保存
            publisher.publishEvent(userLogEvent);

        }
    }

    private void cleanContext() {
        UserContext.remove();
        RequestContext.remove();
        EmailContext.remove();
        DataSourceContext.remove();
    }

}