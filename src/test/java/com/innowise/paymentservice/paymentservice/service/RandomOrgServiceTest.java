package com.innowise.paymentservice.paymentservice.service;

import com.innowise.paymentservice.paymentservice.util.RandomOrgUnavailableException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RandomOrgServiceTest {

    @Mock
    RestTemplate restTemplate;

    @Test
    void getRandomNumber_parsesValidResponse() {
        when(restTemplate.getForObject("http://fake", String.class)).thenReturn(" 42 ");
        RandomOrgService s = new RandomOrgService(restTemplate, "http://fake");
        int v = s.getRandomNumber();
        assertThat(v).isEqualTo(42);
    }

    @Test
    void getRandomNumber_onRestException_throwsRandomOrgUnavailableException() {
        when(restTemplate.getForObject("http://fake", String.class))
                .thenThrow(new RestClientException("exception"));
        RandomOrgService s = new RandomOrgService(restTemplate, "http://fake");
        assertThrows(RandomOrgUnavailableException.class, s::getRandomNumber);
    }

    @Test
    void getRandomNumber_onBadNumber_throwsRandomOrgUnavailableException() {
        when(restTemplate.getForObject("http://fake", String.class)).thenReturn("not-a-number");
        RandomOrgService s = new RandomOrgService(restTemplate, "http://fake");
        assertThrows(RandomOrgUnavailableException.class, s::getRandomNumber);
    }
}