package com.member.easysignapp.util;

import javax.servlet.http.HttpServletRequest;

public class CommonUtil {
    // 모바일인지 체크하는 메서드
    public static boolean isMobile(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        return userAgent != null && userAgent.contains("Mobile");
    }

    // IP 주소 가져오는 메서드
    public static String getClientIp(HttpServletRequest request) {
        String remoteAddr = "";
        if (request != null) {
            remoteAddr = request.getHeader("X-FORWARDED-FOR");
            if (remoteAddr == null || remoteAddr.isEmpty()) {
                remoteAddr = request.getRemoteAddr();
            }
        }
        return remoteAddr;
    }
}
