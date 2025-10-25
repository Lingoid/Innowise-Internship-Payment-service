package com.innowise.paymentservice.paymentservice.config;

import jakarta.annotation.PostConstruct;
import liquibase.Liquibase;
import liquibase.database.DatabaseFactory;
import liquibase.ext.mongodb.database.MongoLiquibaseDatabase;
import liquibase.resource.ClassLoaderResourceAccessor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LiquibaseMongoConfig {

    private final LiquibaseProps properties;

    @PostConstruct
    public void runMigrations() {
        if (!properties.isEnabled()) {
            return;
        }

        try (MongoLiquibaseDatabase database = (MongoLiquibaseDatabase) DatabaseFactory
                .getInstance()
                .openDatabase(
                        properties.getUrl(),
                        null,
                        null,
                        null,
                        new ClassLoaderResourceAccessor());

             Liquibase liquibase = new Liquibase(
                     properties.getChangeLog(),
                     new ClassLoaderResourceAccessor(),
                     database)) {

            liquibase.update();
        } catch (Exception e) {
            throw new RuntimeException(" Failed to run Liquibase migrations", e);
        }
    }
}
