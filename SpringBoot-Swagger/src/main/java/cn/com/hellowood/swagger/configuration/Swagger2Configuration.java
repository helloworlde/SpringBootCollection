package cn.com.hellowood.swagger.configuration;

import cn.com.hellowood.swagger.common.CommonResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Product controller
 *
 * @author HelloWood
 * @date 2018 -01-02 11:38
 */
@EnableSwagger2
@Configuration
public class Swagger2Configuration {

    /**
     * Create rest api docket.
     *
     * @return the docket
     */
    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("HelloWood")
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("cn.com.hellowood.swagger.controller"))
                .paths(PathSelectors.any())
                .build();
    }


    private ApiInfo apiInfo() {
        Contact contact = new Contact("HelloWood", "hellowood.com.cn", "hellowoodes@outlook.com");
        return new ApiInfoBuilder()
                .title("Swagger Application")
                .description("Swagger Application Demo")
                // 服务条款 URL
                .termsOfServiceUrl("http://hellowood.com.cn")
                .contact(contact)
                .version("0.0.1")
                .build();
    }


}



