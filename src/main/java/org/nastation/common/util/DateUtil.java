package org.nastation.common.util;


import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/**
 * @author John | NaChain
 * @since 12/28/2021 1:13
 */
public class DateUtil {

    public static final String DD_MM_YYYY_HH_MM_SS = "dd/MM/yyyy HH:mm:ss";
    public static final String DD_MM_HH_MM_SS = "dd/MM HH:mm:ss";

    public static String formatDateTimeNoYear(LocalDateTime ldt) {

        if (ldt == null) {
            return "-";
        }

        return DateTimeFormatter.ofPattern(DD_MM_HH_MM_SS).format(ldt);
    }

    public static String formatDateTimeFull(LocalDateTime ldt) {

        if (ldt == null) {
            return "-";
        }

        return DateTimeFormatter.ofPattern(DD_MM_YYYY_HH_MM_SS).format(ldt);
    }

    public static String timestampToDateTimeText(long timestamp) {
        int minerCountryZoneOffset = 8;
        LocalDateTime blockTime = LocalDateTime.ofEpochSecond(timestamp / 1000, 0, ZoneOffset.ofHours(minerCountryZoneOffset));
        return DateUtil.formatDateTimeFull(blockTime);
    }



}
