package com.innowise.paymentservice.paymentservice.testdata;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.innowise.paymentservice.paymentservice.dto.PaymentDTO;

import java.io.IOException;

public class TestDataFactory {

    private static final ObjectMapper mapper = new ObjectMapper()
            .findAndRegisterModules();

    public static PaymentDTO getPaymentDTO() throws IOException {
        return mapper.readValue(
                TestDataFactory.class.getResourceAsStream("/test-data/test-data.json"),
                PaymentDTO.class
        );
    }

    private TestDataFactory(){

    }
}
