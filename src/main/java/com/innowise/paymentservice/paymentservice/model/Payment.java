package com.innowise.paymentservice.paymentservice.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Document(collection = "payments")
public class Payment {

    @Id
    private String id;

    private String orderId;
    private String userId;
    private String status;
    private LocalDateTime timestamp;
    private BigDecimal paymentAmount;

}
