package com.learnreactiveprogramming.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

public class FluxAndMonoGeneratorService {

    public Flux<String> namesFlux() {
        return Flux.fromIterable(List.of("Tema", "Vlad", "Anton")).log();
    }

    public Flux<String> namesFluxMap() {
        return Flux.fromIterable(List.of("Tema", "Vlad", "Anton"))
                .map(String::toUpperCase)
                .log();
    }
    public Flux<String> namesFluxFlatMap(int stringLength) {
        return Flux.fromIterable(List.of("Tema", "Vlad", "Anton"))
                .map(String::toUpperCase)
                .filter(s -> s.length() > stringLength)
                .flatMap(this::splitString)
                .log();
    }

    public Flux<String> namesFluxFlatMapAsync(int stringLength) {
        return Flux.fromIterable(List.of("Tema", "Vlad", "Anton"))
                .map(String::toUpperCase)
                .filter(s -> s.length() > stringLength)
                .flatMap(this::splitStringWithDelay)
                .log();
    }
    public Flux<String> namesFluxConcatMap(int stringLength) {
        return Flux.fromIterable(List.of("Tema", "Vlad", "Anton"))
                .map(String::toUpperCase)
                .filter(s -> s.length() > stringLength)
                .concatMap(this::splitStringWithDelay)
                .log();
    }
public Flux<String> namesFluxTransform(int stringLength) {
    Function<Flux<String>, Flux<String>> filterMap = name -> name.map(String::toUpperCase)
            .filter(s -> s.length() > stringLength);
        return Flux.fromIterable(List.of("Tema", "Vlad", "Anton"))
                .transform(filterMap)
                .concatMap(this::splitStringWithDelay)
                .defaultIfEmpty("default")
                .log();
    }
    public Flux<String> namesFluxTransformSwitchIfEmpty(int stringLength) {
    Function<Flux<String>, Flux<String>> filterMap = name -> name.map(String::toUpperCase)
            .filter(s -> s.length() > stringLength)
            .concatMap(this::splitString);
    Flux<String> defaultFlux = Flux.just("default")
            .transform(filterMap);
        return Flux.fromIterable(List.of("Tema", "Vlad", "Anton"))
                .transform(filterMap)
                .switchIfEmpty(defaultFlux)
                .log();
    }

    public Flux<String> concat(){
        var abcFlux = Flux.just("A","B","C");
        var defFlux = Flux.just("D","E","F");

       return Flux.concat(abcFlux,defFlux);
    }
    public Flux<String> merge(){
        var abcFlux = Flux.just("A","B","C")
                .delayElements(Duration.ofMillis(100));
        var defFlux = Flux.just("D","E","F")
                .delayElements(Duration.ofMillis(125));

       return Flux.merge(abcFlux,defFlux);
    }
    public Flux<String> zip(){
        var abcFlux = Flux.just("A","B","C","F");
        var defFlux = Flux.just("D","E","F");

        return Flux.zip(abcFlux,defFlux, (x,y) -> x+y);
    }

    public Flux<String> mergeSequential(){
        var abcFlux = Flux.just("A","B","C")
                .delayElements(Duration.ofMillis(100));
        var defFlux = Flux.just("D","E","F")
                .delayElements(Duration.ofMillis(125));

        return Flux.mergeSequential(abcFlux,defFlux);
    }

    public Flux<String> splitString(String name){
        var charArray = name.split("");
        return Flux.fromArray(charArray);
    }
    public Flux<String> splitStringWithDelay(String name){
        var charArray = name.split("");
        return Flux.fromArray(charArray)
                .delayElements(Duration.ofMillis(new Random().nextInt(1000)));
    }

    public Flux<String> namesFluxFilter(int stringLength) {
        return Flux.fromIterable(List.of("Tema", "Vlad", "Anton"))
                .map(String::toUpperCase)
                .filter(s -> s.length() > stringLength)
                .map(s -> s.length() + "-" + s)
                .log();
    }
    public Mono<List<String>> namesMonoFlatMap(int stringLength) {
        return Mono.just("tema")
                .map(String::toUpperCase)
                .filter(s -> s.length() > stringLength)
                .flatMap(this::splitStringMono);
    }
     public Flux<String> namesMonoFlatMapMany(int stringLength) {
        return Mono.just("tema")
                .map(String::toUpperCase)
                .filter(s -> s.length() > stringLength)
                .flatMapMany(this::splitString);
    }

    private Mono<List<String>> splitStringMono(String s) {
        var charArray = s.split("");
        var charList = List.of(charArray);
        return Mono.just(charList);
    }

    public Flux<String> namesFluxImmutability() {
        var namesFlux = Flux.fromIterable(List.of("Tema", "Vlad", "Anton"));
        namesFlux.map(String::toUpperCase);
        return namesFlux;
    }

    public Mono<String> nameMono() {
        return Mono.just("Tema");
    }

    public static void main(String[] args) {
        FluxAndMonoGeneratorService fluxAndMonoGeneratorService = new FluxAndMonoGeneratorService();
//        fluxAndMonoGeneratorService.namesFlux()
//                .subscribe(System.out::println);
//        fluxAndMonoGeneratorService.nameMono()
//                .subscribe(System.out::println);
        fluxAndMonoGeneratorService.namesFluxFlatMapAsync(4)
                .subscribe(s -> System.out.println(s.length()));
    }
}
