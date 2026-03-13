package com.gym.crm.config;

import com.gym.crm.config.auth.HeaderAuthenticationInterceptor;
import com.gym.crm.config.logging.RequestLoggingInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.util.pattern.PathPatternParser;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = "com.gym.crm")
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final RequestLoggingInterceptor requestLoggingInterceptor;
    private final HeaderAuthenticationInterceptor headerAuthenticationInterceptor;

    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer.favorParameter(false)
                .ignoreAcceptHeader(false)
                .defaultContentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .mediaType("json", org.springframework.http.MediaType.APPLICATION_JSON);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(requestLoggingInterceptor).addPathPatterns("/api/**");
        registry.addInterceptor(headerAuthenticationInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns("/api/trainee/register", "/api/trainer/register");
    }

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        configurer.setPatternParser(new PathPatternParser());
    }

}
