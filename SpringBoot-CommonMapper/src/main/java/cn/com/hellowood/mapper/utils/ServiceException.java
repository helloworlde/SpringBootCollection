package cn.com.hellowood.mapper.utils;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * For handler not expected status
 *
 * @author HelloWood
 * @date 2017-07-11 12:18
 */

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ServiceException extends Exception {

    public ServiceException(String msg, Exception e) {
        super(msg + "\n" + e.getMessage());
    }

    public ServiceException(String msg) {
        super(msg);
    }
}
