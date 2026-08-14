package com.innowise.paymentservice.paymentservice.kafka;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class PaymentProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    private final String topic;

    public PaymentProducer(KafkaTemplate<String, String> kafkaTemplate,
                           @Value("${spring.kafka.topics.create-payment}") String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    public void createPaymentEvent(String message) {
        kafkaTemplate.send(topic, message);
    }
}
