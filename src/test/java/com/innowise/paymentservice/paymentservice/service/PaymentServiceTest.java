package com.innowise.paymentservice.paymentservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.innowise.paymentservice.paymentservice.Repository.PaymentRepository;
import com.innowise.paymentservice.paymentservice.dto.PaymentDTO;
import com.innowise.paymentservice.paymentservice.mapper.PaymentMapper;
import com.innowise.paymentservice.paymentservice.model.Payment;
import com.innowise.paymentservice.paymentservice.util.PaymentNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private PaymentMapper paymentMapper;
    @Mock
    private RandomOrgService randomOrgService;
    @Mock
    private PaymentEventService paymentEventService;

    @InjectMocks
    private PaymentService paymentService;

    private PaymentDTO dto;
    private Payment entity;

    @BeforeEach
    void setUp() throws IOException {
        dto = getPaymentDTO();

        entity = new Payment();
        entity.setOrderId(dto.getOrderId());
        entity.setUserId(dto.getUserId());
        entity.setPaymentAmount(dto.getPaymentAmount());
    }

    private PaymentDTO getPaymentDTO() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
        return mapper.readValue(
                getClass().getResourceAsStream("/test-data/" + "test-data.json"),
                PaymentDTO.class
        );
    }

    @Test
    void createPayment_shouldReturnCreatedPayment() {
        when(paymentMapper.toEntity(dto)).thenReturn(entity);
        when(randomOrgService.getRandomNumber()).thenReturn(2);

        Payment saved = new Payment();
        saved.setId("id1");
        saved.setOrderId(entity.getOrderId());
        saved.setUserId(entity.getUserId());
        saved.setPaymentAmount(entity.getPaymentAmount());
        saved.setStatus("SUCCESS");

        when(paymentRepository.save(any(Payment.class))).thenReturn(saved);
        when(paymentMapper.toDto(saved)).thenAnswer(inv -> {
            Payment p = inv.getArgument(0);
            PaymentDTO r = new PaymentDTO();
            r.setOrderId(p.getOrderId());
            r.setUserId(p.getUserId());
            r.setPaymentAmount(p.getPaymentAmount());
            r.setStatus(p.getStatus());
            r.setTimestamp(p.getTimestamp());
            return r;
        });

        PaymentDTO result = paymentService.createPayment(dto);

        assertAll(
                () -> assertThat(result).isNotNull(),
                () -> assertThat(result.getOrderId()).isEqualTo(dto.getOrderId()),
                () -> assertThat(result.getStatus()).isIn("SUCCESS", "FAILED")
        );

        verify(paymentRepository).save(any(Payment.class));
        verify(paymentEventService).sendCreatePaymentEvent(any());
    }

    @Test
    void getByOrderId_shouldReturnPayment() {
        Payment p = new Payment();
        p.setOrderId("o1");
        when(paymentRepository.findByOrderId("o1")).thenReturn(List.of(p));
        when(paymentMapper.toDto(p)).thenReturn(new PaymentDTO(){{
            setOrderId("o1");
        }});

        List<PaymentDTO> res = paymentService.getByOrderId("o1");

        assertAll(
                () -> assertThat(res).hasSize(1),
                () -> assertThat(res.get(0).getOrderId()).isEqualTo("o1")
        );
    }

    @Test
    void getByOrderId_whenNoPayments_shouldThrowPaymentNotFound() {
        when(paymentRepository.findByOrderId("o1")).thenReturn(List.of());

        assertThrows(PaymentNotFoundException.class,
                () -> paymentService.getByOrderId("o1"));
    }

    @Test
    void getByUserId_shouldReturnPayment() {
        Payment p = new Payment();
        p.setUserId("u1");
        when(paymentRepository.findByUserId("u1")).thenReturn(List.of(p));
        when(paymentMapper.toDto(p)).thenReturn(new PaymentDTO(){{
            setUserId("u1");
        }});

        List<PaymentDTO> res = paymentService.getByUserId("u1");

        assertAll(
                () -> assertThat(res).hasSize(1),
                () -> assertThat(res.get(0).getUserId()).isEqualTo("u1")
        );
    }

    @Test
    void getByStatuses_shouldReturnPayment() {
        Payment p = new Payment();
        p.setStatus("SUCCESS");
        when(paymentRepository.findByStatusIn(List.of("SUCCESS"))).thenReturn(List.of(p));
        when(paymentMapper.toDto(p)).thenReturn(new PaymentDTO(){{
            setStatus("SUCCESS");
        }});

        List<PaymentDTO> res = paymentService.getByStatuses(List.of("SUCCESS"));

        assertAll(
                () -> assertThat(res).hasSize(1),
                () -> assertThat(res.get(0).getStatus()).isEqualTo("SUCCESS")
        );
    }

    @Test
    void getTotalSum_shouldReturnSum() {
        Payment p1 = new Payment(); p1.setPaymentAmount(BigDecimal.valueOf(10));
        Payment p2 = new Payment(); p2.setPaymentAmount(BigDecimal.valueOf(15));
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(1);

        when(paymentRepository.findByTimestampBetween(start, end)).thenReturn(List.of(p1, p2));
        BigDecimal sum = paymentService.getTotalSum(start, end);

        assertThat(sum).isEqualByComparingTo(BigDecimal.valueOf(25));
    }

    @Test
    void getTotalSum_whenNoPayments_shouldReturnZero() {
        when(paymentRepository.findByTimestampBetween(any(), any())).thenReturn(List.of());
        BigDecimal sum = paymentService.getTotalSum(LocalDateTime.now().minusDays(1), LocalDateTime.now());
        assertThat(sum).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void generatePaymentStatus_usesRandomOrgService() {
        when(randomOrgService.getRandomNumber()).thenReturn(10);
        when(paymentMapper.toEntity(any())).thenReturn(entity);
        when(paymentRepository.save(any())).thenReturn(entity);
        when(paymentMapper.toDto(any())).thenReturn(new PaymentDTO());

        paymentService.createPayment(dto);

        verify(randomOrgService, atLeastOnce()).getRandomNumber();
    }

    @Test
    void createPayment_whenSaveFails_shouldThrowException() {
        when(paymentMapper.toEntity(dto)).thenReturn(entity);
        when(paymentRepository.save(any())).thenThrow(new RuntimeException("DB error"));

        assertThrows(RuntimeException.class, () -> paymentService.createPayment(dto));
    }
}