package com.reactivespring.controller;

import com.reactivespring.domain.MovieInfo;
import com.reactivespring.service.MoviesInfoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;

@WebFluxTest(controllers = MoviesInfoController.class)
@AutoConfigureWebTestClient
class MoviesInfoControllerUnitTest {

    @Autowired
    private WebTestClient webTestClient;
    
    @MockBean
     private MoviesInfoService moviesInfoServiceMock;
    static String MOVIES_INFO_URL = "/v1/movieInfos";


    @Test
    void getAll(){

        var movieInfos = List.of(new MovieInfo(null, "Batman Begins",
                        2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15")),
                new MovieInfo(null, "The Dark Knight",
                        2008, List.of("Christian Bale", "HeathLedger"), LocalDate.parse("2008-07-18")),
                new MovieInfo("abc", "Dark Knight Rises",
                        2012, List.of("Christian Bale", "Tom Hardy"), LocalDate.parse("2012-07-20")));


        when(moviesInfoServiceMock.getAll()).thenReturn(Flux.fromIterable(movieInfos));
        webTestClient
                .get()
                .uri(MOVIES_INFO_URL)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBodyList(MovieInfo.class)
                .hasSize(3);
    }

    @Test
    void getById() {
        var id = "abc";
        var movieInfo = new MovieInfo("abc", "Dark Knight Rises",
                        2012, List.of("Christian Bale", "Tom Hardy"), LocalDate.parse("2012-07-20"));
        when(moviesInfoServiceMock.getById(isA(String.class))).thenReturn(Mono.just(movieInfo));

        webTestClient
                .get()
                .uri(MOVIES_INFO_URL + "/{id}", id)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody()
                .jsonPath("$.name").isEqualTo("Dark Knight Rises");

    }


    @Test
    void update() {
        var id = "abc";
        var updatedMovieInfo = new MovieInfo("abc", "Dark Knight Rises 1",
                2013, List.of("Christian Bale1", "Tom Hardy1"), LocalDate.parse("2012-07-20"));
        when(moviesInfoServiceMock.update(isA(MovieInfo.class),isA(String.class))).thenReturn(Mono.just(updatedMovieInfo));

        webTestClient
                .put()
                .uri(MOVIES_INFO_URL + "/{id}", id)
                .bodyValue(updatedMovieInfo)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody(MovieInfo.class)
                .consumeWith(movieInfoEntityExchangeResult -> {
                    var movieInfo = movieInfoEntityExchangeResult.getResponseBody();
                    assert movieInfo != null;
                    assertEquals("Dark Knight Rises 1", movieInfo.getName());
                });
    }

    @Test
    void add() {
        var movieInfo = new MovieInfo("abc", "Batman Begins",
                2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15"));

        when(moviesInfoServiceMock.add(isA(MovieInfo.class))).thenReturn(Mono.just(movieInfo));

        webTestClient
                .post()
                .uri(MOVIES_INFO_URL)
                .bodyValue(movieInfo)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody(MovieInfo.class)
                .consumeWith(movieInfoEntityExchangeResult -> {
                    var savedMovieInfo = movieInfoEntityExchangeResult.getResponseBody();
                    assert Objects.requireNonNull(savedMovieInfo).getMovieInfoId() != null;
                    assertEquals("abc", savedMovieInfo.getMovieInfoId());
                });
    }
    @Test
    void addWithValid() {
        var movieInfo = new MovieInfo("abc", "",
                2, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15"));


        webTestClient
                .post()
                .uri(MOVIES_INFO_URL)
                .bodyValue(movieInfo)
                .exchange()
                .expectStatus()
                .isBadRequest()
//                  .expectBody(String.class)
//                .consumeWith(entityExchangeResult -> {
//                    var errorMessage = entityExchangeResult.getResponseBody();
//                    System.out.println("errorMessage : " + errorMessage);
//                    assert errorMessage!=null;
//                });
//                .expectBody()
//                .jsonPath("$.error").isEqualTo("Bad Request");
                .expectBody(String.class)
                .consumeWith(result -> {
                    var error = result.getResponseBody();
                    assert  error!=null;
                    String expectedErrorMessage = "movieInfo.name must be present";
                    assertEquals(expectedErrorMessage, error);

                });

    }

    @Test
    void delete() {
        var id = "abc";

        when(moviesInfoServiceMock.delete(isA(String.class))).thenReturn(Mono.empty());

        webTestClient
                .delete()
                .uri(MOVIES_INFO_URL + "/{id}", id)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody(Void.class);

    }

    @Test
    void getMovieNotFound() {
        webTestClient.get()
                .uri("/v1/movie/abcd")
                .exchange()
                .expectStatus()
                .isNotFound();
    }
    @Test
    void sink() {
        //given


        //Sinks.Many<Integer> replaySinks = Sinks.many().replay().latest();
        Sinks.Many<Integer> replaySinks = Sinks.many().replay().all();

        var emitResult = replaySinks.tryEmitNext(1);
        System.out.println("emitResult :  " + emitResult);
        replaySinks.emitNext(2, Sinks.EmitFailureHandler.FAIL_FAST);
        /*Sinks.EmitResult emitResult1 = null;
        try {
            emitResult1 = replaySinks.tryEmitNext(errorFunction());
        }catch (Exception ex ){
            System.out.println("Exception is : " + ex);
            System.out.println("emitResult1  :" + emitResult1);
        }
*/

        Flux<Integer> integerFlux = replaySinks.asFlux();
        integerFlux
                .subscribe(s->{
                    System.out.println("Subscriber 1 : " + s);
                });

        Flux<Integer> integerFlux1 = replaySinks.asFlux();

        integerFlux1
                .subscribe(s->{
                    System.out.println("Subscriber 2 : " + s);
                });

    }


    @Test
    void sink_multicast() throws InterruptedException {

        //when

        // It can hold up to 256 elements by default
        Sinks.Many<Integer> multiCast = Sinks.many().multicast().onBackpressureBuffer();

        IntStream.rangeClosed(0,300)
                .forEach(multiCast::tryEmitNext);


        multiCast.tryEmitNext(301);
        multiCast.tryEmitNext(302);

        //then

        Flux<Integer> integerFlux = multiCast.asFlux();
        integerFlux
                .subscribe(s->{
                    System.out.println("Subscriber 1 : " + s);
                });

        multiCast.tryEmitNext(303);

        Flux<Integer> integerFlux1 = multiCast.asFlux();

        integerFlux1
                .subscribe(s->{
                    System.out.println("Subscriber 2 : " + s);
                });

        multiCast.tryEmitNext(4);
    }

}