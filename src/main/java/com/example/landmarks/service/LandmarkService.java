package com.example.landmarks.service;

import com.example.landmarks.model.Landmark;
import com.example.landmarks.repository.LandmarkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LandmarkService {
    private final LandmarkRepository repository;

    @Autowired
    public LandmarkService(LandmarkRepository repository) {
        this.repository = repository;
    }

    public List<Landmark> findAll() {
        return repository.findAll();
    }

    public Landmark findById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public void save(Landmark landmark) {
        repository.save(landmark);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}

