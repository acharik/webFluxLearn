package com.reactivespring.service;

import com.reactivespring.domain.MovieInfo;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface MoviesInfoService {
    Mono<MovieInfo> add(MovieInfo movieInfo);

    Flux<MovieInfo> getAll();

    Mono<MovieInfo> getById(String id);

    Mono<MovieInfo> update(MovieInfo movieInfo, String id);

    Mono<Void> delete(String id);

    Flux<MovieInfo> getByYear(Integer year);
}
