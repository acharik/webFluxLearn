package com.reactivespring.router;


import com.reactivespring.domain.Review;
import com.reactivespring.handler.ReviewHandler;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.path;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class ReviewRouter {

    @Bean
    public RouterFunction<ServerResponse> reviewsRoute(ReviewHandler reviewHandler){
        return route()
                .nest(path("v1/reviews"), builder -> 
                        builder.POST("", reviewHandler::addReview)
                                .GET("",reviewHandler::getAll)
                                .PUT("{id}", reviewHandler::updateReview)
                                .DELETE("{id}", reviewHandler::deleteReview))
                .GET("/v1/helloWorld", (request -> ServerResponse.ok().bodyValue("Hello World")))
                .build();
    }
}
