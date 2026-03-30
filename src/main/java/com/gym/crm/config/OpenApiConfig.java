package com.gym.crm.config;

import com.gym.crm.config.auth.HeaderAuthenticationInterceptor;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@SecurityScheme(
        name = "usernameHeader",
        type = SecuritySchemeType.APIKEY,
        in = SecuritySchemeIn.HEADER,
        paramName = HeaderAuthenticationInterceptor.USERNAME_HEADER,
        description = "Username header for protected endpoints"
)
@SecurityScheme(
        name = "passwordHeader",
        type = SecuritySchemeType.APIKEY,
        in = SecuritySchemeIn.HEADER,
        paramName = HeaderAuthenticationInterceptor.PASSWORD_HEADER,
        description = "Password header for protected endpoints"
)
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Spring Gym CRM API")
                        .version("v1")
                        .description("REST API for trainee, trainer, and training management"));
    }
}


