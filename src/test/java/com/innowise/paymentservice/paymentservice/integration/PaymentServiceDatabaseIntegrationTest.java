package com.innowise.paymentservice.paymentservice.integration;

import com.innowise.paymentservice.paymentservice.Repository.PaymentRepository;
import com.innowise.paymentservice.paymentservice.dto.PaymentDTO;
import com.innowise.paymentservice.paymentservice.model.Payment;
import com.innowise.paymentservice.paymentservice.service.PaymentEventService;
import com.innowise.paymentservice.paymentservice.service.PaymentService;
import com.innowise.paymentservice.paymentservice.service.RandomOrgService;
import com.innowise.paymentservice.paymentservice.testdata.TestDataFactory;
import com.innowise.paymentservice.paymentservice.util.PaymentNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PaymentServiceDatabaseIntegrationTest extends AbstractBaseIntegrationTest {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private PaymentRepository paymentRepository;

    @MockitoBean
    private RandomOrgService randomOrgService;

    @MockitoBean
    private PaymentEventService paymentEventService;

    private PaymentDTO testPaymentDTO;

    @BeforeEach
    void setup() throws IOException {
        paymentRepository.deleteAll();
        testPaymentDTO = TestDataFactory.getPaymentDTO();
    }

    @Test
    void createPayment_shouldSavePaymentAndSendEvent() {
        when(randomOrgService.getRandomNumber()).thenReturn(2);

        PaymentDTO saved = paymentService.createPayment(testPaymentDTO);

        List<Payment> payments = paymentRepository.findAll();

        assertThat(payments).hasSize(1);
        assertThat(saved.getStatus()).isEqualTo("SUCCESS");
        assertThat(saved.getOrderId()).isEqualTo(testPaymentDTO.getOrderId());

        verify(paymentEventService).sendCreatePaymentEvent(saved);
    }

    @Test
    void getByOrderId_shouldReturnPayments_whenExist() {
        when(randomOrgService.getRandomNumber()).thenReturn(1);

        PaymentDTO created = paymentService.createPayment(testPaymentDTO);

        List<PaymentDTO> found = paymentService.getByOrderId(created.getOrderId());
        assertAll(() -> {
            assertThat(found).isNotEmpty();
            assertThat(found.getFirst().getOrderId()).isEqualTo(created.getOrderId());
        });
    }

    @Test
    void getByOrderId_shouldThrowException_whenNotFound() {
        assertThatThrownBy(() -> paymentService.getByOrderId("non-existing"))
                .isInstanceOf(PaymentNotFoundException.class);
    }

    @Test
    void getByUserId_shouldReturnPayments_whenExist() {
        when(randomOrgService.getRandomNumber()).thenReturn(0);
        paymentService.createPayment(testPaymentDTO);

        List<PaymentDTO> found = paymentService.getByUserId(testPaymentDTO.getUserId());
        assertAll(() -> {
            assertThat(found).hasSize(1);
            assertThat(found.getFirst().getUserId()).isEqualTo(testPaymentDTO.getUserId());
        });

    }

    @Test
    void getByUserId_shouldThrowException_whenNotFound() {
        assertThatThrownBy(() -> paymentService.getByUserId("unknown-user"))
                .isInstanceOf(PaymentNotFoundException.class);
    }


    @Test
    void getByStatuses_shouldReturnMatchingPayments() throws IOException {
        when(randomOrgService.getRandomNumber()).thenReturn(2);
        paymentService.createPayment(testPaymentDTO);

        when(randomOrgService.getRandomNumber()).thenReturn(3);
        PaymentDTO another = TestDataFactory.getPaymentDTO();
        another.setOrderId("order-2");
        another.setUserId("user-2");
        paymentService.createPayment(another);

        List<PaymentDTO> found = paymentService.getByStatuses(List.of("SUCCESS"));
        assertAll(() -> {
            assertThat(found).hasSize(1);
            assertThat(found.getFirst().getStatus()).isEqualTo("SUCCESS");
        });
    }

    @Test
    void getTotalSum_shouldReturnCorrectSumForGivenPeriod() {
        Payment payment1 = new Payment(null, "orderA", "user1",
                "SUCCESS", LocalDateTime.now(), BigDecimal.valueOf(50));
        Payment payment2 = new Payment(null, "orderB", "user2",
                "FAILED", LocalDateTime.now(), BigDecimal.valueOf(70));

        paymentRepository.saveAll(List.of(payment1, payment2));

        BigDecimal total = paymentService.getTotalSum(
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().plusDays(1)
        );

        assertThat(total).isEqualByComparingTo(BigDecimal.valueOf(120));
    }

    @Test
    void getTotalSum_shouldReturnZero_whenNoPaymentsInPeriod() {
        BigDecimal total = paymentService.getTotalSum(
                LocalDateTime.now().minusDays(10),
                LocalDateTime.now().minusDays(5)
        );

        assertThat(total).isEqualByComparingTo(BigDecimal.ZERO);
    }
}
