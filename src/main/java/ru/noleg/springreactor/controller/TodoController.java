package ru.noleg.springreactor.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.noleg.springreactor.entity.Todo;
import ru.noleg.springreactor.service.TodoService;

@RestController()
@RequestMapping("/todos")
public class TodoController {

    private final TodoService todoService;

    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    @PostMapping
    public Mono<ResponseEntity<Todo>> createTodo(@RequestBody Todo todo) {
        return todoService.create(todo)
                .map(savedTodo -> ResponseEntity
                        .status(HttpStatus.CREATED)
                        .body(savedTodo)
                )
                .defaultIfEmpty(ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .build()
                );
    }

    @PostMapping("/{id}")
    public Mono<Todo> completeTodo(@PathVariable Long id) {
        return todoService.complete(id);
    }

    @GetMapping("/{id}")
    public Mono<Todo> getTodoById(@PathVariable Long id) {
        return todoService.findById(id);
    }

    @GetMapping()
    public Flux<Todo> getAllTodos() {
        return todoService.getAll();
    }

    @DeleteMapping("/{id}")
    public Mono<Void> deleteTodo(@PathVariable Long id) {
        return todoService.delete(id);
    }
}

