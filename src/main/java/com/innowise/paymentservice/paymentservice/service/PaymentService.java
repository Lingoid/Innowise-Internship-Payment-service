package com.innowise.paymentservice.paymentservice.service;

import com.innowise.paymentservice.paymentservice.Repository.PaymentRepository;
import com.innowise.paymentservice.paymentservice.dto.PaymentDTO;
import com.innowise.paymentservice.paymentservice.mapper.PaymentMapper;
import com.innowise.paymentservice.paymentservice.model.Payment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final RandomOrgService randomOrgService;

    public PaymentService(PaymentRepository paymentRepository, PaymentMapper paymentMapper, RandomOrgService randomOrgService) {
        this.paymentRepository = paymentRepository;
        this.paymentMapper = paymentMapper;
        this.randomOrgService = randomOrgService;
    }

    public PaymentDTO createPayment(PaymentDTO paymentDTO) {
        Payment payment = paymentMapper.toEntity(paymentDTO);
        payment.setTimestamp(LocalDateTime.now());
        payment.setStatus(generatePaymentStatus());
        Payment saved = paymentRepository.save(payment);
        return paymentMapper.toDto(saved);
    }

    public List<PaymentDTO> getByOrderId(String orderId) {
        return paymentRepository.findByOrderId(orderId)
                .stream()
                .map(paymentMapper::toDto)
                .toList();
    }

    public List<PaymentDTO> getByUserId(String userId) {
        return paymentRepository.findByUserId(userId)
                .stream()
                .map(paymentMapper::toDto)
                .toList();
    }

    public List<PaymentDTO> getByStatuses(List<String> statuses) {
        return paymentRepository.findByStatusIn(statuses)
                .stream()
                .map(paymentMapper::toDto)
                .toList();
    }

    public BigDecimal getTotalSum(LocalDateTime start, LocalDateTime end) {
        return paymentRepository.findByTimestampBetween(start, end)
                .stream()
                .map(Payment::getPaymentAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private String generatePaymentStatus() {
        int randomNumber = randomOrgService.getRandomNumber();
        return (randomNumber % 2 == 0) ? "SUCCESS" : "FAILED";
    }
}
