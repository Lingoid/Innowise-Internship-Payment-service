package com.innowise.paymentservice.paymentservice.util;

public class PaymentNotFoundException extends RuntimeException {
    private static final String MESSAGE = "Payment not found";

    public PaymentNotFoundException() {
        super(MESSAGE);
    }
}
