package com.reactivespring.controller;


import com.reactivespring.client.MoviesInfoRestClient;
import com.reactivespring.client.ReviewsRestClient;
import com.reactivespring.domain.Movie;
import com.reactivespring.domain.MovieInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/v1/movies")
@RequiredArgsConstructor
public class MoviesController {

    private final MoviesInfoRestClient moviesInfoRestClient;
    private final ReviewsRestClient reviewsRestClient;

    @GetMapping("/{id}")
    public Mono<Movie> getById(@PathVariable String id) {
        return moviesInfoRestClient.getMovieInfoByIdMovie(id)
                .flatMap(movieInfo -> {
                            var reviewsList = reviewsRestClient.getReviewsByIdMovie(id)
                                    .collectList();
                            return reviewsList.map(reviews -> new Movie(movieInfo, reviews));
                        }
                );
    }
}
