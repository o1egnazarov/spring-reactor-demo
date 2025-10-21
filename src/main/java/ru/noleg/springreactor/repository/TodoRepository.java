package ru.noleg.springreactor.repository;


import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import ru.noleg.springreactor.entity.Todo;

public interface TodoRepository extends ReactiveCrudRepository<Todo, Long> {

}
