package com.psk.backend.config;

import com.google.common.collect.ImmutableList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import springfox.documentation.builders.AuthorizationCodeGrantBuilder;
import springfox.documentation.builders.OAuthBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.SecurityConfiguration;
import springfox.documentation.swagger.web.SecurityConfigurationBuilder;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.time.LocalTime;
import java.util.List;

import static java.util.List.of;


@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean
    public Docket swagger() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.psk.backend"))
                .build()
                .directModelSubstitute(LocalTime.class, String.class)
                .securityContexts(of(securityContext()))
                .securitySchemes(of(securitySchema()))
                .useDefaultResponseMessages(false)
                .genericModelSubstitutes(ResponseEntity.class)
                .ignoredParameterTypes(Authentication.class)
                .apiInfo(apiPublicInfo());
    }

    private SecurityContext securityContext() {
        return SecurityContext.builder()
                .securityReferences(
                        List.of(new SecurityReference("oauth", new AuthorizationScope[]{authorizationScope()}))
                )
                .build();
    }

    private AuthorizationScope authorizationScope() {
        return new AuthorizationScope("all", "All access");
    }

    private ApiInfo apiPublicInfo() {
        return new ApiInfo(
                "PSK API",
                "",
                "0.1.0",
                "",
                new Contact("", "", ""),
                "",
                "",
                ImmutableList.of());
    }

    private OAuth securitySchema() {
        var grantType = new AuthorizationCodeGrantBuilder()
                .tokenEndpoint(new TokenEndpoint("/api/oauth/token", "oauthtoken"))
                .tokenRequestEndpoint(
                        new TokenRequestEndpoint("/api/oauth/authorize", "swagger", passwordEncoder.encode("swagger-secret")))
                .build();

        return new OAuthBuilder()
                .name("oauth")
                .grantTypes(List.of(grantType))
                .scopes(List.of(authorizationScope()))
                .build();

    }

    @Bean
    public SecurityConfiguration security() {
        return SecurityConfigurationBuilder.builder()
                .clientId("swagger")
                .clientSecret("swagger-secret")
                .realm("swagger")
                .appName("swagger")
                .build();
    }
}