package org.anonymous.global.interceptors;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Objects;
import java.util.UUID;

@Component
public class CommonInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        // region 사용자 구분 목적의 hash 생성

        String key = "userHash";
        String userKey = "" + Objects.hash(key);
        String userHash = null;

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(userKey)) {
                    userHash = cookie.getValue();
                    break;
                }
            }
        }

        userHash = StringUtils.hasText(userHash) ? userHash : UUID.randomUUID().toString();
        response.setHeader("Set-Cookie", String.format("%s=%s; path=/; httpOnly; SameSite=None; Secure", userKey, userHash));

        // endregion

        return true;
    }
}

















