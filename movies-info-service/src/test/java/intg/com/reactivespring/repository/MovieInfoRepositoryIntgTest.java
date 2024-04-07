package com.reactivespring.repository;

import com.reactivespring.domain.MovieInfo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest
@ActiveProfiles("test")
class MovieInfoRepositoryIntgTest {

    @Autowired
    private MovieInfoRepository movieInfoRepository;

    @BeforeEach
    void setUp() {
        var movieInfos = List.of(new MovieInfo(null, "Batman Begins",
                        2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15")),
                new MovieInfo(null, "The Dark Knight",
                        2008, List.of("Christian Bale", "HeathLedger"), LocalDate.parse("2008-07-18")),
                new MovieInfo("abc", "Dark Knight Rises",
                        2012, List.of("Christian Bale", "Tom Hardy"), LocalDate.parse("2012-07-20")));

        movieInfoRepository.saveAll(movieInfos)
                .blockLast();    }

    @AfterEach
    void tearDown() {
        movieInfoRepository.deleteAll()
                .block();
    }
    @Test
    void testFindAll() {

        Flux<MovieInfo> movieInfoFlux = movieInfoRepository.findAll().log();

        StepVerifier.create(movieInfoFlux)
                .expectNextCount(3)
                .verifyComplete();

    }
    @Test
    void testFindById() {

        Mono<MovieInfo> movieInfoMono = movieInfoRepository.findById("abc").log();

        StepVerifier.create(movieInfoMono)
                .assertNext(movieInfo -> {
                    assertEquals("Dark Knight Rises", movieInfo.getName());
                })
                .verifyComplete();

    }
    @Test
    void testSave() {
        var movieInfo1 = new MovieInfo(null, "Batman Begins1",
                        2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15"));

        Mono<MovieInfo> movieInfoMono = movieInfoRepository.save(movieInfo1).log();

        StepVerifier.create(movieInfoMono)
                .assertNext(movieInfo -> {
                    assertNotNull(movieInfo.getMovieInfoId());
                    assertEquals("Batman Begins1", movieInfo.getName());
                })
                .verifyComplete();

    }

    @Test
    void updateMovieInfo() {

        var movieInfo = movieInfoRepository.findById("abc").block();
        movieInfo.setYear(2021);

        var savedMovieInfo = movieInfoRepository.save(movieInfo);

        StepVerifier.create(savedMovieInfo)
                .assertNext(movieInfo1 -> {
                    assertNotNull(movieInfo1.getMovieInfoId());
                    assertEquals(2021, movieInfo1.getYear());
                });

    }

    @Test
    void deleteMovieInfo() {

        movieInfoRepository.deleteById("abc").block();

        var movieInfos = movieInfoRepository.findAll();

        StepVerifier.create(movieInfos)
                .expectNextCount(2)
                .verifyComplete();

    }
    @Test
    void testFindByYear() {

        Flux<MovieInfo> movieInfoMono = movieInfoRepository.findByYear(2005).log();

        StepVerifier.create(movieInfoMono)
                .expectNextCount(1)
                .verifyComplete();

    }

    @Test
    void testFindByName() {

        Mono<MovieInfo> movieInfoMono = movieInfoRepository.findByName("Dark Knight Rises").log();

        StepVerifier.create(movieInfoMono)
                .expectNextCount(1)
                .verifyComplete();

    }
}