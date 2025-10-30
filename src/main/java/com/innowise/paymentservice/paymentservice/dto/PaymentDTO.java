package com.innowise.paymentservice.paymentservice.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class PaymentDTO {

    private String id;

    private String orderId;
    private String userId;

    private String status;

    private LocalDateTime timestamp;

    @NotNull(message = "Payment amount cannot be null")
    @Digits(integer = 10, fraction = 2)
    @DecimalMin(value = "0.01", message = "Payment amount must be greater than zero")
    private BigDecimal paymentAmount;
}
