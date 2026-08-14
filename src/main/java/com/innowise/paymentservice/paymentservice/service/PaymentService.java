package com.innowise.paymentservice.paymentservice.service;

import com.innowise.paymentservice.paymentservice.Repository.PaymentRepository;
import com.innowise.paymentservice.paymentservice.dto.PaymentDTO;
import com.innowise.paymentservice.paymentservice.mapper.PaymentMapper;
import com.innowise.paymentservice.paymentservice.model.Payment;
import com.innowise.paymentservice.paymentservice.util.PaymentNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final RandomOrgService randomOrgService;
    private final PaymentEventService paymentEventService;

    public PaymentDTO createPayment(PaymentDTO paymentDTO) {
        Payment payment = paymentMapper.toEntity(paymentDTO);
        payment.setTimestamp(LocalDateTime.now());
        payment.setStatus(generatePaymentStatus());
        Payment saved = paymentRepository.save(payment);
        PaymentDTO result = paymentMapper.toDto(saved);
        paymentEventService.sendCreatePaymentEvent(result);
        return result;
    }

    public List<PaymentDTO> getByOrderId(String orderId) {
        List<PaymentDTO> result = paymentRepository.findByOrderId(orderId)
                .stream()
                .map(paymentMapper::toDto)
                .toList();
        if (result.isEmpty()) {
            throw new PaymentNotFoundException();
        }
        return result;
    }

    public List<PaymentDTO> getByUserId(String userId) {
        List<PaymentDTO> result = paymentRepository.findByUserId(userId)
                .stream()
                .map(paymentMapper::toDto)
                .toList();
        if (result.isEmpty()) {
            throw new PaymentNotFoundException();
        }
        return result;
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
