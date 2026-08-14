package com.innowise.paymentservice.paymentservice.util;

public class PaymentEventException extends RuntimeException {
    private static final String MESSAGE = "failed to handle payment event";

    public PaymentEventException() {
        super(MESSAGE);
    }
}
