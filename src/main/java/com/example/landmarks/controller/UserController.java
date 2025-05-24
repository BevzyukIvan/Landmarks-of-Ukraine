package com.example.landmarks.controller;

import com.example.landmarks.model.Landmark;
import com.example.landmarks.model.User;
import com.example.landmarks.security.AccessControlService;
import com.example.landmarks.service.LandmarkService;
import com.example.landmarks.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Controller
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final LandmarkService landmarkService;
    private final AccessControlService accessControlService;

    public UserController(UserService userService, LandmarkService landmarkService, AccessControlService accessControlService) {
        this.userService = userService;
        this.landmarkService = landmarkService;
        this.accessControlService = accessControlService;
    }

    @GetMapping("/{username}")
    public String showProfile(@PathVariable String username,
                              @RequestParam(defaultValue = "0") int page,
                              @AuthenticationPrincipal User currentUser,
                              Model model) {

        User user = userService.findByUsername(username);
        Pageable pageable = PageRequest.of(page, 6);
        Page<Landmark> landmarkPage = landmarkService.findPageByUserUsername(username, pageable);

        model.addAttribute("user", user);
        model.addAttribute("landmarks", landmarkPage.getContent());
        model.addAttribute("page", landmarkPage);
        model.addAttribute("returnUrl", "/users/" + username);

        if (currentUser != null && !currentUser.getUsername().equals(user.getUsername())) {
            model.addAttribute("isFollowing", userService.isFollowing(currentUser, user));
            model.addAttribute("isFollower", userService.isFollowing(user, currentUser));
        } else {
            model.addAttribute("isFollowing", false);
            model.addAttribute("isFollower", false);
        }

        return "user/profile";
    }


    @GetMapping("/{username}/edit")
    public String editProfile(@PathVariable String username, Model model,
                              @AuthenticationPrincipal User currentUser) {
        User user = userService.findByUsername(username);
        if (!accessControlService.canEditUser(user, currentUser)) {
            return "redirect:/access-denied";
        }

        model.addAttribute("user", user);
        return "user/edit_user";
    }

    @PostMapping("/{username}/edit")
    public String updateProfile(@PathVariable String username,
                                @RequestParam("username") String newUsername,
                                @RequestParam("avatar") MultipartFile file,
                                @RequestParam(value = "deleteAvatar", required = false) boolean deleteAvatar,
                                @AuthenticationPrincipal User currentUser,
                                Model model) throws IOException {
        User user = userService.findByUsername(username);
        if (!accessControlService.canEditUser(user, currentUser)) {
            return "redirect:/access-denied";
        }

        if (!username.equals(newUsername) && userService.usernameExists(newUsername)) {
            model.addAttribute("user", user);
            model.addAttribute("errorMessage", "Користувач з таким іменем вже існує!");
            return "user/edit_user";
        }

        user.setUsername(newUsername);

        if (deleteAvatar) {
            Path path = Path.of(System.getProperty("user.dir") + user.getAvatar());
            Files.deleteIfExists(path);
            user.setAvatar(null);
        } else if (!file.isEmpty()) {
            String uploadDir = System.getProperty("user.dir") + "/uploads/avatars/";
            Files.createDirectories(Path.of(uploadDir));
            String fileName = newUsername + "_" + file.getOriginalFilename();
            Path filePath = Path.of(uploadDir + fileName);
            file.transferTo(filePath.toFile());
            user.setAvatar("/uploads/avatars/" + fileName);
        }

        userService.save(user);

        if (username.equals(currentUser.getUsername())) {
            UsernamePasswordAuthenticationToken newAuth = new UsernamePasswordAuthenticationToken(
                    user,
                    currentUser.getPassword(),
                    currentUser.getAuthorities()
            );
            SecurityContextHolder.getContext().setAuthentication(newAuth);
        }

        return "redirect:/users/" + newUsername;
    }

    @GetMapping("/{username}/followers")
    public String showFollowers(@PathVariable String username,
                                @RequestParam(defaultValue = "followers") String tab,
                                @RequestParam(defaultValue = "") String query,
                                @RequestParam(defaultValue = "0") int page,
                                Model model) {

        User user = userService.findByUsername(username);
        model.addAttribute("user", user);
        model.addAttribute("activeTab", tab);

        Pageable pageable = PageRequest.of(page, 20);
        Page<User> pageResult = "followers".equals(tab)
                ? userService.findFollowersByUser(user, query, pageable)
                : userService.findFollowingByUser(user, query, pageable);

        model.addAttribute(tab, pageResult.getContent());
        model.addAttribute("page", pageResult);
        model.addAttribute("search", query);

        return "user/follow_list";
    }

    @PostMapping("/{username}/follow")
    public String followUser(@PathVariable String username, @AuthenticationPrincipal User currentUser) {
        User target = userService.findByUsername(username);
        if (!currentUser.equals(target)) {
            userService.follow(currentUser, target);
        }
        return "redirect:/users/" + username;
    }

    @PostMapping("/{username}/unfollow")
    public String unfollowUser(@PathVariable String username, @AuthenticationPrincipal User currentUser) {
        User target = userService.findByUsername(username);
        userService.unfollow(currentUser, target);
        return "redirect:/users/" + username;
    }
}