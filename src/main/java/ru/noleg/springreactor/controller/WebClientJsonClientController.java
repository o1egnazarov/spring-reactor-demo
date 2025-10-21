package ru.noleg.springreactor.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.noleg.springreactor.service.WebClientJsonService;

@RestController
@RequestMapping("/client")
public class WebClientJsonClientController {
    private final WebClientJsonService webClientJsonService;

    public WebClientJsonClientController(WebClientJsonService webClientJsonService) {
        this.webClientJsonService = webClientJsonService;
    }

    @GetMapping("/users/{id}")
    public Mono<String> getUserById(@PathVariable Long id) {
        return webClientJsonService.getUsernameById(id);
    }

    @GetMapping("/posts")
    public Flux<String> getAllPosts() {
        return webClientJsonService.getAllPosts();
    }

    @GetMapping("/posts/users/{userId}")
    public Mono<String> getUserWithPosts(@PathVariable Long userId) {
        return webClientJsonService.getUserWithPosts(userId);
    }

    @GetMapping("/users/{userId}/posts/{postId}")
    public Mono<String> combineUserAndPost(@PathVariable Long userId, @PathVariable Long postId) {
        return webClientJsonService.combineUserAndPost(userId, postId);
    }
}
