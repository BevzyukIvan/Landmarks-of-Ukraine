package com.example.landmarks.security;

import com.example.landmarks.model.Landmark;
import com.example.landmarks.model.LandmarkComment;
import com.example.landmarks.model.User;
import com.example.landmarks.service.UserService;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
public class AccessControlService {

    private final UserService userService;

    public AccessControlService(UserService userService) {
        this.userService = userService;
    }

    public boolean canEditLandmark(Landmark landmark, Principal principal) {
        if (principal == null) return false;
        String username = principal.getName();
        return landmark.getUser().getUsername().equals(username) || isAdmin(username);
    }

    public boolean canEditLandmarkComment(LandmarkComment comment, User currentUser) {
        return currentUser != null &&
                (comment.getUser().getUsername().equals(currentUser.getUsername()) || isAdmin(currentUser));
    }

    public boolean canEditUser(User target, User currentUser) {
        return currentUser != null &&
                (target.getUsername().equals(currentUser.getUsername()) || isAdmin(currentUser));
    }

    private boolean isAdmin(User user) {
        return user != null && user.getRole().name().equals("ROLE_ADMIN");
    }

    private boolean isAdmin(String username) {
        User user = userService.findByUsername(username);
        return isAdmin(user);
    }
}
