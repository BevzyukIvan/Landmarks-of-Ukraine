package com.example.landmarks.repository;

import com.example.landmarks.model.Landmark;
import com.example.landmarks.model.LandmarkComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LandmarkCommentRepository extends JpaRepository<LandmarkComment, Long> {
    List<LandmarkComment> findByLandmarkOrderByCreatedAtAsc(Landmark landmark);
}