package com.learnreactiveprogramming.service;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FluxAndMonoGeneratorServiceTest {
    FluxAndMonoGeneratorService fluxAndMonoGeneratorService = new FluxAndMonoGeneratorService();
    @Test
    void namesFlux() {
        var namesFlux = fluxAndMonoGeneratorService.namesFlux();
        StepVerifier.create(namesFlux).expectNext("Tema", "Vlad", "Anton").verifyComplete();
    }

    @Test
    void namesFluxMap() {
       var namesFlux = fluxAndMonoGeneratorService.namesFluxMap();

        StepVerifier.create(namesFlux)
                .expectNext("TEMA", "VLAD", "ANTON")
                .verifyComplete();

    }

    @Test
    void namesFluxImmutability() {
        var namesFlux = fluxAndMonoGeneratorService.namesFluxImmutability();

        StepVerifier.create(namesFlux)
              .expectNext("Tema", "Vlad", "Anton")
              .verifyComplete();
    }

    @Test
    void namesFluxFilter() {
        var namesFlux = fluxAndMonoGeneratorService.namesFluxFilter(4);

        StepVerifier.create(namesFlux)
                .expectNext( "5-ANTON")
                .verifyComplete();

    }

    @Test
    void namesFluxFlatMap() {
        var namesFlux = fluxAndMonoGeneratorService.namesFluxFlatMap(4);
        StepVerifier.create(namesFlux)
                .expectNext( "A","N","T", "O", "N")
                .verifyComplete();
    }

    @Test
    void splitStringWithDelay() {
        var namesFlux = fluxAndMonoGeneratorService.namesFluxFlatMapAsync(3);
        StepVerifier.create(namesFlux)
                .expectSubscription()
                .expectNextCount(13)
                .verifyComplete();
    }

    @Test
    void namesFluxConcatMap() {
        var namesFlux = fluxAndMonoGeneratorService.namesFluxConcatMap(3);
        StepVerifier.create(namesFlux)
                .expectSubscription()
                .expectNext("T","E","M", "A","V","L","A","D","A","N","T","O","N")
                .verifyComplete();
    }

    @Test
    void namesMonoFlatMap() {
        var namesFlux = fluxAndMonoGeneratorService.namesMonoFlatMap(3);
        StepVerifier.create(namesFlux)
                .expectNext(List.of( "T","E","M", "A"))
                .verifyComplete();
    }

    @Test
    void namesMonoFlatMapMany() {
        var namesFlux = fluxAndMonoGeneratorService.namesMonoFlatMapMany(3);
        StepVerifier.create(namesFlux)
                .expectNext( "T","E","M", "A")
                .verifyComplete();
    }

    @Test
    void namesFluxTransform() {
        var namesFlux = fluxAndMonoGeneratorService.namesFluxTransform(3);
        StepVerifier.create(namesFlux)
                .expectSubscription()
                .expectNextCount(13)
                .verifyComplete();
    }
    @Test
    void namesFluxTransform2() {
        var namesFlux = fluxAndMonoGeneratorService.namesFluxTransform(5);
        StepVerifier.create(namesFlux)
                .expectSubscription()
                .expectNext("default")
                .verifyComplete();
    }

    @Test
    void namesFluxTransformSwitchIfEmpty() {
        var namesFlux = fluxAndMonoGeneratorService.namesFluxTransformSwitchIfEmpty(5);
        StepVerifier.create(namesFlux)
                .expectSubscription()
                .expectNext("D","E","F","A","U","L","T")
                .verifyComplete();
    }

    @Test
    void concat() {
        var namesFlux = fluxAndMonoGeneratorService.concat();
        StepVerifier.create(namesFlux)
                .expectSubscription()
                .expectNext("A","B","C","D","E","F")
                .verifyComplete();
    }

    @Test
    void merge() {
        var namesFlux = fluxAndMonoGeneratorService.merge();
        StepVerifier.create(namesFlux)
                .expectSubscription()
                .expectNext("A","D","B","E","C","F")
                .verifyComplete();
    }

    @Test
    void mergeSequential() {
        var namesFlux = fluxAndMonoGeneratorService.mergeSequential();
        StepVerifier.create(namesFlux)
                .expectSubscription()
                .expectNext("A","B","C","D","E","F")
                .verifyComplete();
    }

    @Test
    void zip() {
        var namesFlux = fluxAndMonoGeneratorService.zip();
        StepVerifier.create(namesFlux)
                .expectSubscription()
                .expectNext("AD","BE","CF")
                .verifyComplete();
    }
}