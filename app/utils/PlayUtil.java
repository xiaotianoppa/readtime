
package utils;

import org.apache.commons.lang.StringUtils;

import play.mvc.Http.Cookie;
import play.mvc.Http.Request;

public class PlayUtil {

    public static Object EMPTY_OBJ = new Object();

    public static final void sleepQuietly(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
        }
    }

    public static String getCookieString(Request request, String key) {
        Cookie cookie = request.cookies.get(key);
        return cookie == null ? null : cookie.value;
    }

    public enum OS {
        LINUX, WINDOWS
    }

    public static OS os = null;

    public static OS getOS() {
        if (os != null) {
            return os;
        }

        if (StringUtils.startsWith(System.getProperty("os.name").toLowerCase(), "linux")) {
            os = OS.LINUX;
        } else {
            os = OS.WINDOWS;
        }
        return os;
    }
    
    public static String trimValue(String value) {
        if (StringUtils.isEmpty(value)) {
            return "";
        }
        return value.trim();
    }
}
