package ru.noleg.springreactor.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;


@Table("todos")
public record Todo(
        @Id @Column("id") Long id,
        @Column("title") String title,
        @Column("completed") boolean completed
) {
}
