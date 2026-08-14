package com.innowise.paymentservice.paymentservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.innowise.paymentservice.paymentservice.dto.PaymentDTO;
import com.innowise.paymentservice.paymentservice.kafka.PaymentProducer;
import com.innowise.paymentservice.paymentservice.util.PaymentEventException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentEventServiceTest {

    @Mock
    PaymentProducer paymentProducer;
    @Mock
    ObjectMapper objectMapper;

    @InjectMocks
    PaymentEventService eventService;

    @Test
    void sendCreatePaymentEvent_sendsJson() throws Exception {
        PaymentDTO dto = new PaymentDTO();
        dto.setOrderId("o1");
        dto.setStatus("SUCCESS");
        dto.setTimestamp(LocalDateTime.now());

        when(objectMapper.writeValueAsString(any())).thenReturn("{\"x\":1}");

        eventService.sendCreatePaymentEvent(dto);

        verify(paymentProducer).createPaymentEvent("{\"x\":1}");
    }

    @Test
    void sendCreatePaymentEvent_throwsPaymentEventException_onJsonProcessingException() throws Exception {
        PaymentDTO dto = new PaymentDTO();
        dto.setOrderId("o1");
        dto.setStatus("SUCCESS");
        dto.setTimestamp(LocalDateTime.now());

        when(objectMapper.writeValueAsString(any()))
                .thenThrow(new JsonProcessingException("bad") {});

        assertThrows(PaymentEventException.class, () -> eventService.sendCreatePaymentEvent(dto));

        verify(paymentProducer, never()).createPaymentEvent(any());
    }


}