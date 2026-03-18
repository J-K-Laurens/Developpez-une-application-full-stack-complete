package com.openclassrooms.mddapi.model;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Topic entity representing a topic or category.
 * Articles can be associated with multiple topics.
 */
@Entity
@Table(name = "TOPICS")
public class Topic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", length = 50)
    private String name;

    @Column(name = "date")
    private LocalDateTime date;

    public Topic() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public LocalDateTime getDate() { return date; }
    public void setDate(LocalDateTime date) { this.date = date; }
}
