package com.example.landmarks.service;

import com.example.landmarks.model.Landmark;
import com.example.landmarks.model.User;
import com.example.landmarks.repository.LandmarkRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class LandmarkService {

    private final LandmarkRepository landmarkRepository;

    public LandmarkService(LandmarkRepository landmarkRepository) {
        this.landmarkRepository = landmarkRepository;
    }

    public Landmark findById(Long id) {
        return landmarkRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Памʼятку не знайдено"));
    }

    public void save(Landmark landmark) {
        landmarkRepository.save(landmark);
    }

    public void delete(Landmark landmark) {
        landmarkRepository.delete(landmark);
    }

    public Page<Landmark> findPageByUserUsername(String username, Pageable pageable) {
        return landmarkRepository.findByUserUsernameOrderByCreatedAtDesc(username, pageable);
    }

    public Page<Landmark> findLandmarksFromUserIds(List<Long> userIds, Pageable pageable) {
        return landmarkRepository.findByUserIdInOrderByCreatedAtDesc(userIds, pageable);
    }

    public Page<Landmark> findAllPaginated(Pageable pageable) {
        return landmarkRepository.findAllByOrderByCreatedAtDesc(pageable);
    }

    public Page<Landmark> searchByName(String name, Pageable pageable) {
        return landmarkRepository.findByNameContainingIgnoreCase(name, pageable);
    }
}

