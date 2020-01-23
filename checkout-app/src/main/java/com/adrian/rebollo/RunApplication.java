package com.adrian.rebollo;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.adrian.rebollo.platform.interceptor.LoggingInterceptor;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableSwagger2
@SpringBootApplication(scanBasePackages = "com.adrian.*")
@EntityScan("com.adrian.*")
public class RunApplication implements WebMvcConfigurer {

    public static void main(String[] args) {
        Executors.newSingleThreadScheduledExecutor().schedule(() -> SpringApplication.run(RunApplication.class, args), 10, TimeUnit.SECONDS);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoggingInterceptor()).addPathPatterns("/**");
    }

    @Bean
    public Docket docket() {
        return new Docket(DocumentationType.SWAGGER_2)
            .select()
            .apis(RequestHandlerSelectors.withClassAnnotation(RestController.class))
            .paths(PathSelectors.any())
            .build()
            .apiInfo(generateApiInfo());
    }

    private ApiInfo generateApiInfo() {
        return new ApiInfo("Checkout API",
                "This service is a checkout API.",
                "Version 1.0.0",
                "urn:tos",
                "adria.rebollo@gmail.com",
                "Apache 2.0",
                "http://www.apache.org/licenses/LICENSE-2.0");
    }
}
