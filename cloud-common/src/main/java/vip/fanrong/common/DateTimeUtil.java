package vip.fanrong.common;

import org.apache.commons.lang3.StringUtils;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by Rong on 2017/12/13.
 */
public class DateTimeUtil {
    private static final String TIME_ZONE_GMT8 = "GMT+08:00";

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

    /**
     * 时间标准格式 yyyy-MM-dd HH:mm:ss
     *
     * @param time
     * @return
     */
    public static Date getDateFromSimpleFormatter(String time) {
        return getDate(time, "yyyy-MM-dd HH:mm:ss");
    }

    public static Date getDate(String time, String formatter) {
        if (StringUtils.isEmpty(time)) {
            return null;
        }
        DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(formatter).withZone(ZoneId.of(TIME_ZONE_GMT8));
        ZonedDateTime dateTime = ZonedDateTime.parse(time, FORMATTER);
        return Date.from(dateTime.toInstant());
    }
}
