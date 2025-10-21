package ru.noleg.springreactor.service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;
import reactor.util.retry.RetryBackoffSpec;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.TimeoutException;

@Service
public class WebClientJsonService {

    private final WebClient webClient;

    public WebClientJsonService(WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<String> getUsernameById(Long id) {
        return webClient
                .get()
                .uri("/users/{id}", id)
                .retrieve()
                .bodyToMono(String.class)
                .retryWhen(customRetry())
                .doOnNext(body -> System.out.println("User: " + body + "\n"));
    }

    public Flux<String> getAllPosts() {
        return webClient
                .get()
                .uri("/posts")
                .retrieve()
                .bodyToFlux(String.class)
                .retryWhen(customRetry())
                .doOnNext(body -> System.out.println("Post: " + body));
    }

    public Mono<String> combineUserAndPost(Long userId, Long postId) {
        Mono<String> user = webClient
                .get()
                .uri("/users/{id}", userId)
                .retrieve()
                .bodyToMono(String.class)
                .retryWhen(customRetry());

        Mono<String> post = webClient
                .get()
                .uri("/posts/{id}", postId)
                .retrieve()
                .bodyToMono(String.class)
                .retryWhen(customRetry());

        return Mono
                .zip(user, post)
                .map(tuple -> "User: " + tuple.getT1() + "\nPost: " + tuple.getT2());
    }

    public Mono<String> getUserWithPosts(Long userId) {
        Mono<String> user = webClient
                .get()
                .uri("users/{id}", userId)
                .retrieve()
                .bodyToMono(String.class)
                .retryWhen(customRetry());

        Flux<String> posts = webClient
                .get()
                .uri("/posts?userId={id}", userId)
                .retrieve()
                .bodyToFlux(String.class)
                .retryWhen(customRetry());

        return  user.zipWith(posts.collectList())
                .map(tuple -> "User: " + tuple.getT1() + "\nPosts: " + tuple.getT2());
    }

    private RetryBackoffSpec customRetry() {
        return Retry.backoff(3, Duration.ofSeconds(1))
                .filter(e -> e instanceof IOException || e instanceof TimeoutException);
    }
}
