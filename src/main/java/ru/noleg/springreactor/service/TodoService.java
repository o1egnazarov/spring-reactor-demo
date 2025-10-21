package ru.noleg.springreactor.service;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.noleg.springreactor.entity.Todo;
import ru.noleg.springreactor.repository.TodoRepository;

@Service
public class TodoService {
    private final TodoRepository todoRepository;

    public TodoService(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    public Mono<Todo> create(Todo todo) {
        return todoRepository.save(todo);
    }

    public Mono<Todo> findById(Long id) {
        return todoRepository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Todo not found")));
    }

    public Flux<Todo> getAll() {
        return todoRepository.findAll();
    }

    public Mono<Void> delete(Long id) {
        return todoRepository.deleteById(id);
    }

    public Mono<Todo> complete(Long id) {
        return todoRepository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Todo not found")))
                .map(todo -> new Todo(todo.id(), todo.title(), true))
                .flatMap(todoRepository::save);
    }
}
