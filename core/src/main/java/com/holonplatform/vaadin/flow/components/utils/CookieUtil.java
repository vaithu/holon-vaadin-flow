package com.holonplatform.vaadin.flow.components.utils;

import com.vaadin.flow.server.VaadinService;
import jakarta.servlet.http.Cookie;

import java.util.Arrays;
import java.util.Optional;

public class CookieUtil {

    public static Cookie saveCookie(final String cookieName, final String value) {
        final Cookie cookie = new Cookie(cookieName, value);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60 * 24 * 30);
        VaadinService.getCurrentResponse().addCookie(cookie);
        return cookie;
    }

    public static void deleteCookie(final String cookieName) {
        final Cookie cookie = new Cookie(cookieName, "");
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        VaadinService.getCurrentResponse().addCookie(cookie);
    }

    public static Optional<Cookie> getCookie(final String cookieName) {
        final Cookie[] cookies = VaadinService.getCurrentRequest().getCookies();
        return Arrays.stream(cookies).filter(c -> c.getName().equals(cookieName)).findFirst();
    }
}
