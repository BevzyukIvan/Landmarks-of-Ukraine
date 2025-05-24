package com.example.landmarks.controller;

import com.example.landmarks.model.Landmark;
import com.example.landmarks.model.User;
import com.example.landmarks.service.LandmarkService;
import com.example.landmarks.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class FeedController {

    private final LandmarkService landmarkService;
    private final UserService userService;

    public FeedController(LandmarkService landmarkService, UserService userService) {
        this.landmarkService = landmarkService;
        this.userService = userService;
    }

    @GetMapping("/feed")
    public String feed(@AuthenticationPrincipal User currentUser,
                       @RequestParam(defaultValue = "0") int page,
                       HttpServletRequest request,
                       Model model) {
        if (currentUser == null) {
            return "redirect:/login";
        }

        Pageable pageable = PageRequest.of(page, 10);
        List<Long> followedIds = userService.findFollowedUserIdsByUsername(currentUser.getUsername());
        Page<Landmark> landmarkPage = landmarkService.findLandmarksFromUserIds(followedIds, pageable);

        model.addAttribute("landmarks", landmarkPage.getContent());
        model.addAttribute("page", landmarkPage);
        model.addAttribute("returnUrl", request.getRequestURI());

        return "feed";
    }
}
