package ru.noleg.springreactor;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import ru.noleg.springreactor.entity.Todo;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class WebClientTest {

    @Autowired
    WebTestClient webClient;

    @Test
    @WithMockUser(roles = "USER")
    void test_getTodos() {
        webClient.get()
                .uri("/todos")
                .header(HttpHeaders.ACCEPT, "application/json")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Todo.class);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testAddNewStudent() {
        Todo newTodo = new Todo(
                1L, "some title", false
        );

        webClient.post()
                .uri("/todos")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(newTodo), Todo.class)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.title").isEqualTo(newTodo.title())
                .jsonPath("$.completed").isEqualTo(newTodo.completed());
    }
}
