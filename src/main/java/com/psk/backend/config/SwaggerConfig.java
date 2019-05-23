package com.psk.backend.config;

import com.fasterxml.classmate.TypeResolver;
import com.google.common.collect.ImmutableList;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import springfox.documentation.builders.AlternateTypeBuilder;
import springfox.documentation.builders.AlternateTypePropertyBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.AlternateTypeRule;
import springfox.documentation.schema.AlternateTypeRuleConvention;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.SecurityConfiguration;
import springfox.documentation.swagger.web.SecurityConfigurationBuilder;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.annotation.Resource;
import java.lang.reflect.Type;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

import static java.util.List.of;
import static springfox.documentation.schema.AlternateTypeRules.newRule;


@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Resource
    private PasswordEncoder passwordEncoder;

    @Resource
    private Environment environment;

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
                "1.0",
                "",
                new Contact("", "", ""),
                "",
                "",
                ImmutableList.of());
    }

    private OAuth securitySchema() {
        AuthorizationScope authorizationScope = authorizationScope();
        LoginEndpoint loginEndpoint = new LoginEndpoint("/api/oauth/authorize");
        GrantType grantType = new ImplicitGrant(loginEndpoint, "access_token");
        return new OAuth("oauth", of(authorizationScope), of(grantType));
    }

    @Bean
    public SecurityConfiguration security() {
        return SecurityConfigurationBuilder.builder()
                .clientId(environment.getRequiredProperty("app.security.oauth2.swagger.client"))
                .clientSecret(environment.getRequiredProperty("app.security.oauth2.swagger.secret"))
                .scopeSeparator(" ")
                .useBasicAuthenticationWithAccessCodeGrant(true)
                .realm("swagger")
                .appName("swagger")
                .build();
    }

    @Bean
    public AlternateTypeRuleConvention pageableConvention(
            final TypeResolver resolver) {
        return new AlternateTypeRuleConvention() {

            @Override
            public int getOrder() {
                return Ordered.HIGHEST_PRECEDENCE;
            }

            @Override
            public List<AlternateTypeRule> rules() {
                return Arrays.asList(
                        newRule(resolver.resolve(Pageable.class), resolver.resolve(pageableMixin()))
                );
            }
        };
    }

    private Type pageableMixin() {
        return new AlternateTypeBuilder()
                .fullyQualifiedClassName(
                        String.format("%s.generated.%s",
                                Pageable.class.getPackage().getName(),
                                Pageable.class.getSimpleName()))
                .withProperties(Arrays.asList(
                        property(Integer.class, "page"),
                        property(Integer.class, "size"),
                        property(String.class, "sort")
                ))
                .build();
    }

    private AlternateTypePropertyBuilder property(Class<?> type, String name) {
        return new AlternateTypePropertyBuilder()
                .withName(name)
                .withType(type)
                .withCanRead(true)
                .withCanWrite(true);
    }
}