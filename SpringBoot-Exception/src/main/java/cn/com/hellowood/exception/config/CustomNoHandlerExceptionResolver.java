package cn.com.hellowood.exception.config;

import cn.com.hellowood.exception.common.CustomResponseContent;
import org.springframework.boot.autoconfigure.web.servlet.error.AbstractErrorController;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

/**
 * @author HelloWood
 */
@Controller
@RequestMapping("/error")
public class CustomNoHandlerExceptionResolver extends AbstractErrorController {

    public CustomNoHandlerExceptionResolver(ErrorAttributes errorAttributes) {
        super(errorAttributes);
    }

    @Override
    public String getErrorPath() {
        return null;
    }

    @RequestMapping
    @ResponseBody
    public ResponseEntity<?> error(HttpServletRequest request, HttpServletResponse response) {
        Optional<String> originRequestUri = Optional.ofNullable((String) request.getAttribute("javax.servlet.error.request_uri"));
        String reasonPhrase = HttpStatus.valueOf(response.getStatus()).getReasonPhrase();

        CustomResponseContent responseContent = CustomResponseContent.builder()
                                                                     .code(response.getStatus())
                                                                     .status("fail")
                                                                     .message(originRequestUri.orElse("/error") + ":" + reasonPhrase)
                                                                     .build();

        return new ResponseEntity<>(responseContent, HttpStatus.valueOf(response.getStatus()));
    }

    @RequestMapping(produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView pageError(HttpServletRequest request, HttpServletResponse response) {
        return new ModelAndView(String.format("error/%d.html", response.getStatus()));
    }

}
