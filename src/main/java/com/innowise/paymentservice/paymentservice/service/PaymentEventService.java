package com.innowise.paymentservice.paymentservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.innowise.paymentservice.paymentservice.dto.PaymentDTO;
import com.innowise.paymentservice.paymentservice.kafka.PaymentProducer;
import com.innowise.paymentservice.paymentservice.util.PaymentEventException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PaymentEventService {

    private final PaymentProducer paymentProducer;
    private final ObjectMapper objectMapper;

    public void sendCreatePaymentEvent(PaymentDTO paymentDTO) {
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("orderId", paymentDTO.getOrderId());
            payload.put("status", paymentDTO.getStatus());
            payload.put("timestamp", paymentDTO.getTimestamp());

            String message = objectMapper.writeValueAsString(payload);
            paymentProducer.createPaymentEvent(message);

        } catch (Exception e) {
            throw new PaymentEventException();
        }
    }
}
