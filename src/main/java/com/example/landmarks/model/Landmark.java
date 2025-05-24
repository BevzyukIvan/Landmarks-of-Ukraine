// 🔁 Оновлений Landmark з коментарями та датою створення
package com.example.landmarks.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@Table(name = "landmarks")
public class Landmark {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty(message = "Назва не може бути порожньою")
    private String name;

    private String location;

    @Size(max = 1000, message = "Опис не повинен перевищувати 1000 символів")
    @Column(length = 1000)
    private String description;

    private String imagePath;

    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "landmark", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<LandmarkComment> comments = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }

    public Landmark(String name, String location, String description, String imagePath, User user) {
        this.name = name;
        this.location = location;
        this.description = description;
        this.imagePath = imagePath;
        this.user = user;
    }
}