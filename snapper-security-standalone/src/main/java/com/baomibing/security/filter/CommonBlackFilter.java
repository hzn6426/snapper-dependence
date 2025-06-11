package com.baomibing.security.filter;


import com.baomibing.tool.constant.UserHeaderConstant;
import com.baomibing.tool.constant.WebConstant;
import com.baomibing.tool.util.Checker;
import com.baomibing.web.common.MultiHeaderHttpServletRequest;
import inet.ipaddr.IPAddressString;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * CommonBlackFilter
 *
 * @author zening 2023/9/6 10:00
 * @version 1.0.0
 **/
public class CommonBlackFilter extends BaseFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        MultiHeaderHttpServletRequest mrequest = new MultiHeaderHttpServletRequest(request);
        String ip = getIP(mrequest);
        addHeader(request, UserHeaderConstant.USER_IP, ip);
        if (matchWhiteList(mrequest.getRequestURI())) {
            filterChain.doFilter(mrequest, response);
        }
        if (matchIps(ip)) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return;
        }

        if (Checker.beEmpty(getHeader(mrequest, WebConstant.CONTENT_TYPE))) {
            addHeader(mrequest, WebConstant.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        }

        filterChain.doFilter(mrequest, response);
    }

    private static final CopyOnWriteArraySet<String> blacks = new CopyOnWriteArraySet<>();
    private static final CopyOnWriteArraySet<String> whites =  new CopyOnWriteArraySet<>();

    public void addBlacks(Set<String> urls) {
        blacks.addAll(urls);
    }

    @Override
    public void addWhites(Set<String> urls) {
        if (Checker.beNotEmpty(urls)) {
            whites.addAll(urls);
        }
    }

    private boolean matchWhiteList(String url) {
        return whites.stream().anyMatch(w -> pathMatch.match(w, url));
    }
    private boolean matchIps(String ip) {
        IPAddressString ipString = new IPAddressString(ip);
        for (String ipSegment : blacks) {
            IPAddressString segmentString = new IPAddressString(ipSegment);
            if (segmentString.contains(ipString)) return true;
        }
        return false;
    }
}
