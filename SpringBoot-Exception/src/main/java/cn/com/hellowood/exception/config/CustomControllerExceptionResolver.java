package cn.com.hellowood.exception.config;

import cn.com.hellowood.exception.common.CustomResponseContent;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.stream.Collectors;

/**
 * @author HelloWood
 */
// 生效需要删除注释
// @RestControllerAdvice
// @ControllerAdvice
public class CustomControllerExceptionResolver {

    @ExceptionHandler({NullPointerException.class})
    @ResponseBody
    public ResponseEntity<?> handlerNullPointerException(HttpServletRequest request, Throwable throwable) {
        CustomResponseContent responseContent = CustomResponseContent.builder()
                                                                     .code(500)
                                                                     .status("fail")
                                                                     .message("空指针异常")
                                                                     .build();
        return new ResponseEntity<>(responseContent, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({IllegalArgumentException.class})
    @ResponseBody
    public ResponseEntity<?> handlerIllegalArgumentException(HttpServletRequest request, Throwable throwable) {
        CustomResponseContent responseContent = CustomResponseContent.builder()
                                                                     .code(400)
                                                                     .status("fail")
                                                                     .message("参数错误")
                                                                     .build();
        return new ResponseEntity<>(responseContent, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    @ResponseBody
    public ResponseEntity<?> handlerMethodArgumentNotValidException(HttpServletRequest request, MethodArgumentNotValidException e) {
        String errorMessage = e.getBindingResult()
                               .getAllErrors()
                               .stream()
                               .map(f -> ((FieldError) f).getField() + ":" + f.getDefaultMessage())
                               .collect(Collectors.joining(";"));

        CustomResponseContent responseContent = CustomResponseContent.builder()
                                                                     .code(400)
                                                                     .status("fail")
                                                                     .message(errorMessage)
                                                                     .build();
        return new ResponseEntity<>(responseContent, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({Exception.class})
    @ResponseBody
    public ResponseEntity<?> handlerException(HttpServletRequest request, Throwable throwable) {
        CustomResponseContent responseContent = CustomResponseContent.builder()
                                                                     .code(500)
                                                                     .status("fail")
                                                                     .message(throwable.getMessage())
                                                                     .build();
        return new ResponseEntity<>(responseContent, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
