package com.adrian.rebollo;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.test.context.ActiveProfiles;

@Configuration
@ActiveProfiles("dev")
@SpringBootApplication(scanBasePackages = "com.adrian")
@EnableJpaRepositories(basePackages = "com.adrian")
@EntityScan(basePackages = "com.adrian.rebollo.*")
@EnableRetry
public class TestConfiguration {
}
