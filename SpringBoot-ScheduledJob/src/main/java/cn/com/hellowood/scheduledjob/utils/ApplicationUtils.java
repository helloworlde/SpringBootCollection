package cn.com.hellowood.scheduledjob.utils;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;

import static cn.com.hellowood.scheduledjob.common.CommonConstant.DATE_TIME;
import static cn.com.hellowood.scheduledjob.common.CommonConstant.DATE_TIME_FORMATTER;

/**
 * The type Application utils.
 */
public class ApplicationUtils {

    /**
     * Format date string.
     *
     * @param date the date
     * @return the string
     */
    public static String formatDate(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_TIME);
        return simpleDateFormat.format(date);
    }

    /**
     * Get current datetime
     *
     * @return string
     */
    public static String currentDateTime() {
        return LocalDateTime.now().format(DATE_TIME_FORMATTER);
    }
}
