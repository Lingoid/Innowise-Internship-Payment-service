package com.innowise.paymentservice.paymentservice.service;

import com.innowise.paymentservice.paymentservice.util.RandomOrgUnavailableException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;


@Service
public class RandomOrgService {

    private final RestTemplate restTemplate;
    private final String apiUrl;

    public RandomOrgService(RestTemplate restTemplate, @Value("${random-org.url}") String apiUrl) {
        this.restTemplate = restTemplate;
        this.apiUrl = apiUrl;
    }

    public int getRandomNumber() {
        try {
            String response = restTemplate.getForObject(apiUrl, String.class);
            return Integer.parseInt(response.trim());
        } catch (RestClientException | NumberFormatException e) {
            throw new RandomOrgUnavailableException();
        }
    }
}
