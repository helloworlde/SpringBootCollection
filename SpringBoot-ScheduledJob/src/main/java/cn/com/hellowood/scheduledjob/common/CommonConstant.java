package cn.com.hellowood.scheduledjob.common;

import java.time.format.DateTimeFormatter;

/**
 * Common constant
 *
 * @author HelloWood
 * @date 2017-07-11 15:46
 */
public class CommonConstant {

    /**
     * Request result message
     */
    public static final String DEFAULT_SUCCESS_MESSAGE = "success";
    public static final String DEFAULT_FAIL_MESSAGE = "fail";
    public static final String NO_RESULT_MESSAGE = "no result";

    /**
     * Operation status
     */
    public static final String SUCCESS = "SUCCESS";
    public static final String ERROR = "ERROR";

    /**
     * Error or exception message
     */
    public static final String DB_ERROR_MESSAGE = "Database Error";
    public static final String SERVER_ERROR_MESSAGE = "Server Error";


    /**
     * Time
     */
    public static final String DATE_TIME = "yyyy-MM-dd HH:mm:ss";

    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

}
