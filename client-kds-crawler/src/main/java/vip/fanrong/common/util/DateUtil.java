package vip.fanrong.common.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Rong on 2017/7/14.
 */
public class DateUtil {

    private static DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static String getDateNow() {
        Date date = new Date();
        String time = format.format(date);
        return time;
    }

}
