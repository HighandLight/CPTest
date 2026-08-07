package com.highandlight.CPTest.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "sample_data")
public class SampleData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id; // 21억 이하로 진행하기에, Integer로 선언

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int value;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public Integer getId() { return id; }
    public String getName() { return name; }
    public int getValue() { return value; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
