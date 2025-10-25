package com.innowise.paymentservice.paymentservice.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Setter
@Getter
@ConfigurationProperties(prefix = "spring.liquibase")
public class LiquibaseProps {
    private boolean enabled;
    private String url;
    private String changeLog;
}
