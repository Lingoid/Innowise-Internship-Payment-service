package com.innowise.paymentservice.paymentservice.util;

public class InvalidDigitsFormatException extends RuntimeException {
    private static final String MESSAGE = "Invalid number format: maximum 10 digits and 2 decimal places allowed";

    public InvalidDigitsFormatException() {
        super(MESSAGE);
    }
}
