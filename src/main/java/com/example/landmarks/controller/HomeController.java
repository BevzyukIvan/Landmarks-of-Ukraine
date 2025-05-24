package com.example.landmarks.controller;

import com.example.landmarks.model.Landmark;
import com.example.landmarks.service.LandmarkService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeController {

    private final LandmarkService landmarkService;

    public HomeController(LandmarkService landmarkService) {
        this.landmarkService = landmarkService;
    }

    @GetMapping("/")
    public String home(@RequestParam(defaultValue = "0") int page,
                       HttpServletRequest request,
                       Model model) {
        Pageable pageable = PageRequest.of(page, 10);
        Page<Landmark> landmarkPage = landmarkService.findAllPaginated(pageable);

        model.addAttribute("landmarks", landmarkPage.getContent()); // тільки список
        model.addAttribute("page", landmarkPage);                   // для пагінації
        model.addAttribute("returnUrl", request.getRequestURI());

        return "home";
    }
}
