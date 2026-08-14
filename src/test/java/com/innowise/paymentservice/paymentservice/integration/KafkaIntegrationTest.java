package com.innowise.paymentservice.paymentservice.integration;

import com.innowise.paymentservice.paymentservice.dto.PaymentDTO;
import com.innowise.paymentservice.paymentservice.kafka.PaymentProducer;
import com.innowise.paymentservice.paymentservice.service.PaymentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class KafkaIntegrationTest extends AbstractBaseIntegrationTest {


    @Autowired
    private PaymentProducer paymentProducer;

    @MockitoSpyBean
    private PaymentService paymentService;

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.topic}")
    private String topic;

    @Captor
    ArgumentCaptor<PaymentDTO> argumentCaptor = ArgumentCaptor.forClass(PaymentDTO.class);


    @Test
    void whenProduced_thenConsumed() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);

        doAnswer(invocation -> {
            argumentCaptor.capture();
            latch.countDown();
            return invocation.callRealMethod();
        }).when(paymentService).createPayment(argumentCaptor.capture());


        String message =
            """
            {
                "orderId": "123",
                "userId": "user1",
                "timestamp": "2025-11-02T11:00:00"
            }
            """;

        paymentProducer.createPaymentEvent(message);

        boolean completed = latch.await(5, TimeUnit.SECONDS);
        assertTrue(completed, "Метод createPayment не был вызван");

        PaymentDTO paymentDTO = argumentCaptor.getValue();
        assertAll(() -> {
            assertNotNull(paymentDTO);
            assertEquals("123", paymentDTO.getOrderId());
            assertEquals("user1", paymentDTO.getUserId());
        });
    }
}
