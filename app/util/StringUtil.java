package util;

/**
 * Created by jiangecho on 15/11/1.
 */
public class StringUtil {
    public static boolean isEmputy(String string) {
        return string == null ? true : string.trim().length() == 0;
    }
}
