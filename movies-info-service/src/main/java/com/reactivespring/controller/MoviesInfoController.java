package com.reactivespring.controller;

import com.reactivespring.domain.MovieInfo;
import com.reactivespring.service.MoviesInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import javax.validation.Valid;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class MoviesInfoController {

    private final MoviesInfoService moviesInfoService;
    Sinks.Many<MovieInfo> movieInfoSink = Sinks.many().replay().latest();

    @GetMapping("/movieInfos")
    public Flux<MovieInfo> getAll() {
        return moviesInfoService.getAll();
    }

    @GetMapping("/movieInfos/{id}")
    public Mono<ResponseEntity<MovieInfo>> getById(@PathVariable String id) {
        return moviesInfoService.getById(id)
                .map(ResponseEntity.ok()::body)
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }

    @GetMapping("/movieInfosByYear")
    public Flux<MovieInfo> getByYear(@RequestParam Integer year) {
        return moviesInfoService.getByYear(year);
    }

    @DeleteMapping("/movieInfos/{id}")
    public Mono<Void> delete(@PathVariable String id) {
        return moviesInfoService.delete(id);
    }

    @PostMapping("/movieInfos")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<MovieInfo> addMovieInfo(@RequestBody @Valid MovieInfo movieInfo) {
        return moviesInfoService.add(movieInfo)
                .doOnNext(savedMovieInfo -> movieInfoSink.tryEmitNext(savedMovieInfo));

    }
    @GetMapping(value = "/movieInfos/stream", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<MovieInfo> streamMovieInfos() {

        return movieInfoSink.asFlux();
    }
    @PutMapping("/movieInfos/{id}")
    public Mono<ResponseEntity<MovieInfo>> update(@RequestBody MovieInfo movieInfo,@PathVariable String id) {
        return moviesInfoService.update(movieInfo,id)
                .map(ResponseEntity.ok()::body)
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }
}
