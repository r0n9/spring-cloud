package vip.fanrong.common;

import java.util.Date;
import java.util.TimeZone;

/**
 * Created by Rong on 2017/12/13.
 */
public class DateTimeUtil {

    public static Date getTimeNowGMT8() {
        return changeTimeZoneToGMT8(new Date());
    }

    public static Date changeTimeZoneToGMT8(Date date) {
        return changeTimeZone(date, TimeZone.getDefault(), TimeZone.getTimeZone("Asia/Shanghai"));
    }

    public static Date changeTimeZone(Date date, TimeZone oldZone, TimeZone newZone) {
        Date dateTmp = null;
        if (date != null) {
            int timeOffset = oldZone.getRawOffset() - newZone.getRawOffset();
            dateTmp = new Date(date.getTime() - timeOffset);
        }
        return dateTmp;
    }
}
