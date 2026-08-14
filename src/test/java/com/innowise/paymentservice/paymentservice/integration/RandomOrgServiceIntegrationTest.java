package com.innowise.paymentservice.paymentservice.integration;

import com.github.tomakehurst.wiremock.WireMockServer;
import static com.github.tomakehurst.wiremock.client.WireMock.get;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.innowise.paymentservice.paymentservice.service.RandomOrgService;
import com.innowise.paymentservice.paymentservice.util.RandomOrgUnavailableException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RandomOrgServiceIntegrationTest extends AbstractBaseIntegrationTest {

    @Autowired
    private RandomOrgService randomOrgService;

    @Test
    void testRandomNumberSuccess() {
        WireMock wireMock = new WireMock(
                wireMockContainer.getHost(),
                wireMockContainer.getMappedPort(8080)
        );

        wireMock.register(get("/random")
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody("42")));

        int result = randomOrgService.getRandomNumber();
        assertEquals(42, result);
    }

    @Test
    void testRandomNumberFailure() {
        WireMock wireMock = new WireMock(
                wireMockContainer.getHost(),
                wireMockContainer.getMappedPort(8080)
        );

        wireMock.register(get("/random")
                .willReturn(aResponse().withStatus(500)));

        assertThrows(RandomOrgUnavailableException.class, () -> randomOrgService.getRandomNumber());
    }
}
