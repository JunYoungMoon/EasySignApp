package com.member.easysignapp.util;

import javax.servlet.http.HttpServletRequest;

public class WebUtil {
    private static final String MOBILE_USER_AGENT = "Mobile";

    public static boolean isMobile(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        return userAgent != null && userAgent.contains(MOBILE_USER_AGENT);
    }
}
