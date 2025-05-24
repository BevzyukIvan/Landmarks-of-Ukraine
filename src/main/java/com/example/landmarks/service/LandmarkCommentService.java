package com.example.landmarks.service;

import com.example.landmarks.model.Landmark;
import com.example.landmarks.model.LandmarkComment;
import com.example.landmarks.repository.LandmarkCommentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LandmarkCommentService {

    private final LandmarkCommentRepository commentRepository;

    public LandmarkCommentService(LandmarkCommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    public List<LandmarkComment> getCommentsForLandmark(Long landmarkId) {
        Landmark landmark = new Landmark();
        landmark.setId(landmarkId);
        return commentRepository.findByLandmarkOrderByCreatedAtAsc(landmark);
    }

    public LandmarkComment findById(Long id) {
        return commentRepository.findById(id).orElseThrow();
    }

    public void save(LandmarkComment comment) {
        commentRepository.save(comment);
    }

    public void delete(LandmarkComment comment) {
        commentRepository.delete(comment);
    }
}