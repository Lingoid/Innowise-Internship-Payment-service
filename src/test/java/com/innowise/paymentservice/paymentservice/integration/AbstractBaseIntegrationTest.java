package com.innowise.paymentservice.paymentservice.integration;


import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.KafkaContainer;
import org.wiremock.integrations.testcontainers.WireMockContainer;

import java.util.List;
import java.util.Map;

@Testcontainers
@SpringBootTest
public abstract class AbstractBaseIntegrationTest {

    @Container
    @ServiceConnection
    static MongoDBContainer mongoDBContainer =
            new MongoDBContainer("mongo:8.2");

    @Container
    static WireMockContainer wireMockContainer =
            new WireMockContainer("wiremock/wiremock:3x-alpine");

    @Container
    static KafkaContainer kafkaContainer =
            new KafkaContainer("apache/kafka:4.1.0");

    @DynamicPropertySource
    static void kafkaProperties(DynamicPropertyRegistry registry) {
        kafkaContainer.start();
        String bootstrapServers = kafkaContainer.getBootstrapServers();
        registry.add("spring.kafka.bootstrap-servers", () -> bootstrapServers);

        try (AdminClient adminClient = AdminClient.create(
                Map.of("bootstrap.servers", bootstrapServers))) {
            NewTopic topic = new NewTopic("test-topic", 1, (short) 1);
            adminClient.createTopics(List.of(topic)).all().get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        registry.add("spring.kafka.topic", () -> "test-topic");
    }

    @DynamicPropertySource
    static void registerWireMock(DynamicPropertyRegistry registry) {
        if (!wireMockContainer.isRunning()) {
            wireMockContainer.start();
        }
        registry.add("random-org.url", () ->
                "http://" + wireMockContainer.getHost() + ":" + wireMockContainer.getMappedPort(8080) + "/random");
    }


}
