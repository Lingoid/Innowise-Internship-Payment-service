package com.innowise.paymentservice.paymentservice.kafka;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.innowise.paymentservice.paymentservice.dto.PaymentDTO;
import com.innowise.paymentservice.paymentservice.service.PaymentService;
import com.innowise.paymentservice.paymentservice.util.RandomAmountGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class PaymentConsumer {

    private final PaymentService paymentService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "${spring.kafka.topics.create-order}", groupId = "${spring.kafka.consumer.group-id}")
    public void handleCreateOrder(String message) {
        try {
            JsonNode jsonNode = objectMapper.readTree(message);

            PaymentDTO paymentDTO = new PaymentDTO();
            paymentDTO.setOrderId(jsonNode.get("orderId").asText());
            paymentDTO.setUserId(jsonNode.get("userId").asText());
            paymentDTO.setTimestamp(LocalDateTime.parse(jsonNode.get("timestamp").asText()));
            paymentDTO.setPaymentAmount(RandomAmountGenerator.generateRandomAmount());

            paymentService.createPayment(paymentDTO);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
