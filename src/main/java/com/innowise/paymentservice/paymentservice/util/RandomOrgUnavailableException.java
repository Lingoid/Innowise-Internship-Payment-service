package com.innowise.paymentservice.paymentservice.util;

public class RandomOrgUnavailableException extends RuntimeException {

    private static final String MESSAGE = "Failed to get random number";

    public RandomOrgUnavailableException() {
        super(MESSAGE);
    }
}
