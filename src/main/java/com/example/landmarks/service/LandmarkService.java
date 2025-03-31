package com.example.landmarks.service;

import com.example.landmarks.model.Landmark;
import com.example.landmarks.model.User;
import com.example.landmarks.repository.LandmarkRepository;
import com.example.landmarks.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LandmarkService {

    private final LandmarkRepository landmarkRepository;
    private final UserRepository userRepository;

    public LandmarkService(LandmarkRepository landmarkRepository, UserRepository userRepository) {
        this.landmarkRepository = landmarkRepository;
        this.userRepository = userRepository;
    }

    public List<Landmark> findAll() {
        return landmarkRepository.findAll();
    }

    public Landmark findById(Long id) {
        return landmarkRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Landmark not found"));
    }

    public void save(Landmark landmark) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (landmark.getId() == null) {
            landmark.setUser(user);
        } else {
            Landmark existingLandmark = landmarkRepository.findById(landmark.getId())
                    .orElseThrow(() -> new EntityNotFoundException("Landmark not found"));

            if (!existingLandmark.getUser().equals(user) && !isAdmin(user)) {
                throw new AccessDeniedException("You can only edit your own landmarks!");
            }
        }

        landmarkRepository.save(landmark);
    }

    public void delete(Long id) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Landmark landmark = landmarkRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Landmark not found"));

        if (!landmark.getUser().equals(user) && !isAdmin(user)) {
            throw new AccessDeniedException("You can only delete your own landmarks!");
        }

        landmarkRepository.delete(landmark);
    }

    private boolean isAdmin(User user) {
        return user.getRole().name().equals("ROLE_ADMIN");
    }
}
