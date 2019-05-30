package com.psk.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.datetime.standard.DateTimeFormatterRegistrar;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
@EnableAsync
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addFormatters(FormatterRegistry registry) {
        DateTimeFormatterRegistrar reigistrar = new DateTimeFormatterRegistrar();
        reigistrar.setUseIsoFormat(true);
        reigistrar.registerFormatters(registry);
    }
}

