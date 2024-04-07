package com.reactivespring.service.impl;

import com.reactivespring.domain.MovieInfo;
import com.reactivespring.repository.MovieInfoRepository;
import com.reactivespring.service.MoviesInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class MoviesInfoServiceImpl implements MoviesInfoService {

    private final MovieInfoRepository movieInfoRepository;

    @Override
    public Mono<MovieInfo> add(MovieInfo movieInfo) {
        return movieInfoRepository.save(movieInfo);
    }

    @Override
    public Flux<MovieInfo> getAll() {
        return movieInfoRepository.findAll();
    }

    @Override
    public Mono<MovieInfo> getById(String id) {
        return movieInfoRepository.findById(id);
    }

    @Override
    public Mono<MovieInfo> update(MovieInfo movieInfo, String id) {
        return movieInfoRepository.findById(id)
                .flatMap(movieInfoNew -> {
                    movieInfoNew.setCast(movieInfo.getCast());
                    movieInfoNew.setYear(movieInfo.getYear());
                    movieInfoNew.setName(movieInfo.getName());
                    movieInfoNew.setReleaseDate(movieInfo.getReleaseDate());
                    return movieInfoRepository.save(movieInfoNew);
                });
    }

    @Override
    public Mono<Void> delete(String id) {
        return movieInfoRepository.deleteById(id);
    }

    @Override
    public Flux<MovieInfo> getByYear(Integer year) {
        return movieInfoRepository.findByYear(year);
    }
}
