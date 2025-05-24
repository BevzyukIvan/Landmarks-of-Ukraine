package com.example.landmarks.controller;

import com.example.landmarks.model.Landmark;
import com.example.landmarks.model.User;
import com.example.landmarks.service.LandmarkService;
import com.example.landmarks.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class SearchController {

    private final UserService userService;
    private final LandmarkService landmarkService;

    public SearchController(UserService userService, LandmarkService landmarkService) {
        this.userService = userService;
        this.landmarkService = landmarkService;
    }

    @GetMapping("/search")
    public String searchPage(
            @RequestParam(value = "q", required = false) String query,
            @RequestParam(value = "type", defaultValue = "user") String type,
            @RequestParam(defaultValue = "0") int page,
            Model model) {

        Pageable pageable = PageRequest.of(page, 20);

        model.addAttribute("query", query);
        model.addAttribute("type", type);

        if ("landmark".equals(type)) {
            Page<Landmark> landmarkPage = (query != null && !query.isBlank())
                    ? landmarkService.searchByName(query, pageable)
                    : Page.empty();

            model.addAttribute("landmarkResults", landmarkPage.getContent());
            model.addAttribute("landmarkPage", landmarkPage);
        } else {
            Page<User> userPage = (query != null && !query.isBlank())
                    ? userService.searchByUsername(query, pageable)
                    : Page.empty();

            model.addAttribute("userResults", userPage.getContent());
            model.addAttribute("page", userPage);
        }

        return "search";
    }

}
