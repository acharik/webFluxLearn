package com.reactivespring;


import com.github.tomakehurst.wiremock.client.WireMock;
import com.reactivespring.domain.Movie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;


import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Objects;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWebTestClient
@AutoConfigureWireMock(port = 8084)
@TestPropertySource(
        properties = {
                "restClient.moviesInfoUrl=http://localhost:8084/v1/movieInfos",
                "restClient.reviewsUrl=http://localhost:8084/v1/reviews"
        }
)
public class MoviesControllerIntgTest {
    
    @Autowired
    WebTestClient webTestClient;
    @Test
    void getMovieInfoByIdMovie() {

        var movieId = "abc";

        stubFor(WireMock.get(urlEqualTo("v1/movieInfos/" + movieId))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\n" +
                                "  \"movieInfoId\": \"1\",\n" +
                                "  \"name\": \"Batman Begins\",\n" +
                                "  \"year\": 2005,\n" +
                                "  \"cast\": [\n" +
                                "    \"Christian Bale\",\n" +
                                "    \"Michael Cane\"\n" +
                                "  ],\n" +
                                "  \"release_date\": \"2005-06-15\"\n" +
                                "}")));

        stubFor(WireMock.get(urlPathEqualTo("/v1/reviews" + movieId))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("[\n" +
                                "  {\n" +
                                "    \"reviewId\": \"1\",\n" +
                                "    \"movieInfoId\": 1,\n" +
                                "    \"comment\": \"Awesome Movie\",\n" +
                                "    \"rating\": 9.0\n" +
                                "  },\n" +
                                "  {\n" +
                                "    \"reviewId\": \"2\",\n" +
                                "    \"movieInfoId\": 1,\n" +
                                "    \"comment\": \"Excellent Movie\",\n" +
                                "    \"rating\": 8.0\n" +
                                "  }\n" +
                                "]")));

        webTestClient.get()
                .uri("v1/movies/{id}", movieId)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(Movie.class)
                .consumeWith(movieEntityExchangeResult -> {
                    var movie = movieEntityExchangeResult.getResponseBody();
                    assert Objects.requireNonNull(movie).getReviewList().size() == 2;
                    assertEquals("Batman Begins", movie.getMovieInfo().getName());
                        }
                );

    }
}
