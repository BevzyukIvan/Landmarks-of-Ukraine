package com.example.landmarks.service;

import com.example.landmarks.model.User;
import com.example.landmarks.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public User register(User user) {
        return userRepository.save(user);
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Користувача не знайдено"));
    }

    public boolean usernameExists(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    public void save(User user) {
        userRepository.save(user);
    }

    public Page<User> searchByUsername(String query, Pageable pageable) {
        return userRepository.findByUsernameContainingIgnoreCase(query, pageable);
    }

    public boolean isFollowing(User currentUser, User profileOwner) {
        return userRepository.isUserFollowing(currentUser, profileOwner);
    }

    @Transactional
    public void follow(User follower, User followed) {
        userRepository.insertFollow(follower.getId(), followed.getId());
    }

    @Transactional
    public void unfollow(User follower, User followed) {
        userRepository.deleteFollow(follower.getId(), followed.getId());
    }

    public Page<User> findFollowersByUser(User user, String query, Pageable pageable) {
        return userRepository.findFollowersByUsernameAndQuery(user.getUsername(), query, pageable);
    }

    public Page<User> findFollowingByUser(User user, String query, Pageable pageable) {
        return userRepository.findFollowingByUsernameAndQuery(user.getUsername(), query, pageable);
    }

    public List<Long> findFollowedUserIdsByUsername(String username) {
        return userRepository.findFollowedUserIdsByUsername(username);
    }
}

