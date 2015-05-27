package util;

import java.util.Calendar;

/**
 * Created by jiangecho on 15/5/28.
 */
public class TimeUtil {
    public static long getTodayStartMillis(){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime().getTime();
    }
}
