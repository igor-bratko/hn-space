package com.us.spaces.hn.auth;

import java.net.HttpCookie;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class CookieFormatter {
    private static final ZoneId GMT = ZoneId.of("GMT");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US).withZone(GMT);

    public static String toString(HttpCookie cookie) {
        StringBuilder sb = new StringBuilder();
        sb.append(cookie.getName()).append('=').append(cookie.getValue());
        if (hasText(cookie.getPath())) {
            sb.append("; Path=").append(cookie.getPath());
        }
        if (hasText(cookie.getDomain())) {
            sb.append("; Domain=").append(cookie.getDomain());
        }
        var maxAge = cookie.getMaxAge();
        if (!(maxAge < 0)) {
            sb.append("; Max-Age=").append(maxAge);
            sb.append("; Expires=");
            long millis = (maxAge > 0 ? System.currentTimeMillis() + (maxAge * 1000) : 0);
            sb.append(formatDate(millis));
        }
        if (cookie.getSecure()) {
            sb.append("; Secure");
        }
        if (cookie.isHttpOnly()) {
            sb.append("; HttpOnly");
        }

        return sb.toString();
    }

    private static boolean hasText(String str) {
        return (str != null && !str.isBlank());
    }

    private static String formatDate(long date) {
        Instant instant = Instant.ofEpochMilli(date);
        ZonedDateTime time = ZonedDateTime.ofInstant(instant, GMT);
        return DATE_FORMATTER.format(time);
    }
}
