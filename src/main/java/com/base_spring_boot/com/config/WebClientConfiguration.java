package com.base_spring_boot.com.config;


import com.base_spring_boot.com.applications.base.controller.exception.FunctionnalException;
import com.base_spring_boot.com.applications.base.controller.exception.TechnicalErrorException;
import com.base_spring_boot.com.applications.base.controller.exception.UnauthorizedException;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Configuration
public class WebClientConfiguration {

    public static ExchangeFilterFunction errorHandler() {
        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
            if (clientResponse.statusCode().is5xxServerError()) {
                return clientResponse.bodyToMono(String.class)
                        .flatMap(errorBody -> Mono.error(new TechnicalErrorException(errorBody)));
            } else if (clientResponse.statusCode().value() == 401) {
                return clientResponse.bodyToMono(String.class)
                        .flatMap(errorBody -> Mono.error(new UnauthorizedException(errorBody)));
            } else if (clientResponse.statusCode().is4xxClientError()) {
                return clientResponse.bodyToMono(String.class)
                        .flatMap(errorBody -> Mono.error(new FunctionnalException(errorBody)));
            } else {
                return Mono.just(clientResponse);
            }
        });
    }

    @Bean
    public WebClient webClient() {
        return WebClient.builder().filter(errorHandler()).build();
    }
}
