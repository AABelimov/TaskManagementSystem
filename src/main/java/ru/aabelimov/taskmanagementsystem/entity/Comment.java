package ru.aabelimov.taskmanagementsystem.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "comments")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String comment;
    private Long timestamp;

    @ManyToOne
    private Task task;

    @ManyToOne
    private User author;
}
