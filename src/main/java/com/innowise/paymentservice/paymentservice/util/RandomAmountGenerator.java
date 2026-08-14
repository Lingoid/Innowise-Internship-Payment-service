package com.innowise.paymentservice.paymentservice.util;

import java.math.BigDecimal;

public final class RandomAmountGenerator {

    public static BigDecimal generateRandomAmount(){
        double randomAmount = 1 + Math.random() * 999;
        return BigDecimal.valueOf(Math.round(randomAmount * 100.0) / 100.0);
    }

    private RandomAmountGenerator(){

    }
}
