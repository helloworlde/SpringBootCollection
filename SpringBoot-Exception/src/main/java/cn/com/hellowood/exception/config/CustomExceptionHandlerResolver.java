package cn.com.hellowood.exception.config;

import cn.com.hellowood.exception.common.CustomResponseContent;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;

/**
 * @author HelloWood
 */
public class CustomExceptionHandlerResolver implements HandlerExceptionResolver {

    private static final ObjectMapper OBJECT_MAPPER;

    static {
        OBJECT_MAPPER = new ObjectMapper();
        OBJECT_MAPPER.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        OBJECT_MAPPER.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        OBJECT_MAPPER.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
    }

    @Override
    public ModelAndView resolveException(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) {

        String message = "服务器错误";

        if (o instanceof HandlerMethod) {
            if (e instanceof IllegalArgumentException) {
                message = "参数错误";
            } else if (e instanceof SecurityException) {
                message = "不允许访问";
            } else if (e instanceof NullPointerException) {
                message = "空指针异常";
            }
        } else if (e instanceof NoHandlerFoundException) {
            message = "未找到相应资源";
        } else if (e instanceof HttpMediaTypeNotSupportedException) {
            message = "请求类型不支持";
        }

        httpServletResponse.setCharacterEncoding(StandardCharsets.UTF_8.name());
        httpServletResponse.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);

        try {
            httpServletResponse.getWriter()
                               .write(
                                       OBJECT_MAPPER.writeValueAsString(
                                               CustomResponseContent.builder()
                                                                    .code(500)
                                                                    .status("fail")
                                                                    .message(message)
                                                                    .build()
                                       )
                               );
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return new ModelAndView();
    }
}
