package com.reactivespring.client;

import com.reactivespring.domain.MovieInfo;
import com.reactivespring.domain.Review;
import com.reactivespring.exception.MoviesInfoClientException;
import com.reactivespring.exception.ReviewsClientException;
import com.reactivespring.exception.ReviewsServerException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class ReviewsRestClient {

    @Value("${restClient.reviewsUrl}")
    private String reviewsUrl;

    private final WebClient webClient;


    public Flux<Review> getReviewsByIdMovie(String idMovie) {
      var url = UriComponentsBuilder.fromHttpUrl(reviewsUrl)
                .queryParam("movieInfoId", idMovie)
                .buildAndExpand().toUriString();
        return webClient.get()
                .uri(url)
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, clientResponse -> {
                    if (clientResponse.statusCode() == HttpStatus.NOT_FOUND) {
                      return Mono.empty();
                    }
                    return clientResponse.bodyToMono(String.class)
                            .flatMap(response ->
                                    Mono.error(new ReviewsClientException(response)));
                })
                .onStatus(HttpStatus::is5xxServerError, clientResponse -> Mono.error(new ReviewsServerException("ERROR")))
                .bodyToFlux(Review.class)
                .log();
    }
}
