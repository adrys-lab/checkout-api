package com.adrian.rebollo;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public class PostgresContainerExtension implements BeforeAllCallback, AfterAllCallback {

    @Container
    public static PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer("postgres:11")
            .withDatabaseName("integration-tests-db")
            .withUsername("test")
            .withPassword("test");

    public static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues.of(
                    "spring.flyway.url:" + postgreSQLContainer.getJdbcUrl(),
                    "spring.flyway.user:" + postgreSQLContainer.getUsername(),
                    "spring.flyway.password:" + postgreSQLContainer.getPassword(),
                    "spring.datasource.url:" + postgreSQLContainer.getJdbcUrl(),
                    "spring.datasource.username:" + postgreSQLContainer.getUsername(),
                    "spring.datasource.password:" + postgreSQLContainer.getPassword()
                    ).applyTo(configurableApplicationContext.getEnvironment());
        }
    }

    @Override
    public void afterAll(ExtensionContext extensionContext) {
        postgreSQLContainer.stop();
    }

    @Override
    public void beforeAll(ExtensionContext extensionContext) {
        postgreSQLContainer.start();
    }

}
