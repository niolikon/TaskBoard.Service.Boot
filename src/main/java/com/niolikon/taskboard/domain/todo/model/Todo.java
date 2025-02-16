package com.niolikon.taskboard.domain.todo.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Table(name = "todos")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class Todo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Exclude
    @Setter(AccessLevel.NONE)
    private Long id;

    @EqualsAndHashCode.Include
    @Setter
    private String title;

    @EqualsAndHashCode.Include
    @Setter
    private String description;

    @EqualsAndHashCode.Include
    @Setter
    private Boolean isCompleted;

    @EqualsAndHashCode.Include
    @Setter
    private Date dueDate;

    @EqualsAndHashCode.Include
    @Setter
    private String ownerUid;

    public void updateFrom(Todo other) {
        if (other == null) {
            return;
        }
        if (other.title != null && !other.title.isEmpty()) {
            this.title = other.title;
        }
        if (other.description != null && !other.description.isEmpty()) {
            this.description = other.description;
        }
        if (other.isCompleted != null) {
            this.isCompleted = other.isCompleted;
        }
        if (other.dueDate != null) {
            this.dueDate = other.dueDate;
        }
    }
}
