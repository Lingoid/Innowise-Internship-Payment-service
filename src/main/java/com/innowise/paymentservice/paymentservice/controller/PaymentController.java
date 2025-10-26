package com.innowise.paymentservice.paymentservice.controller;

import com.innowise.paymentservice.paymentservice.dto.PaymentDTO;
import com.innowise.paymentservice.paymentservice.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    public ResponseEntity<PaymentDTO> createPayment(@Valid @RequestBody PaymentDTO paymentDTO) {
        PaymentDTO created = paymentService.createPayment(paymentDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<PaymentDTO>> getByOrderId(@PathVariable String orderId) {
        return ResponseEntity.ok(paymentService.getByOrderId(orderId));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PaymentDTO>> getByUserId(@PathVariable String userId) {
        return ResponseEntity.ok(paymentService.getByUserId(userId));
    }

    @GetMapping("/statuses")
    public ResponseEntity<List<PaymentDTO>> getByStatuses(@RequestParam List<String> statuses) {
        return ResponseEntity.ok(paymentService.getByStatuses(statuses));
    }

    @GetMapping("/total")
    public ResponseEntity<BigDecimal> getTotalSum(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return ResponseEntity.ok(paymentService.getTotalSum(start, end));
    }
}
