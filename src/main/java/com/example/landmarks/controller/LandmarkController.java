package com.example.landmarks.controller;

import com.example.landmarks.model.Landmark;
import com.example.landmarks.model.LandmarkComment;
import com.example.landmarks.model.User;
import com.example.landmarks.security.AccessControlService;
import com.example.landmarks.service.LandmarkCommentService;
import com.example.landmarks.service.LandmarkService;
import com.example.landmarks.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.UUID;

@Controller
@RequestMapping("/landmarks")
public class LandmarkController {

    private final LandmarkService landmarkService;
    private final LandmarkCommentService commentService;
    private final UserService userService;
    private final AccessControlService accessControlService;

    @Autowired
    public LandmarkController(LandmarkService landmarkService,
                              LandmarkCommentService commentService,
                              UserService userService,
                              AccessControlService accessControlService) {
        this.landmarkService = landmarkService;
        this.commentService = commentService;
        this.userService = userService;
        this.accessControlService = accessControlService;
    }

    @GetMapping("/new")
    public String newLandmark(Model model) {
        model.addAttribute("landmark", new Landmark());
        return "landmarks/new";
    }

    @PostMapping("/new")
    public String create(@ModelAttribute("landmark") @Valid Landmark landmark,
                         BindingResult result,
                         @RequestParam("file") MultipartFile file,
                         Principal principal,
                         Model model) throws IOException {
        if (result.hasErrors() || file.isEmpty()) {
            model.addAttribute("uploadError", file.isEmpty() ? "Оберіть зображення" : null);
            return "landmarks/new";
        }

        String uploadDir = System.getProperty("user.dir") + "/uploads/landmarks/";
        Files.createDirectories(Paths.get(uploadDir));
        String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path filePath = Paths.get(uploadDir + filename);
        file.transferTo(filePath.toFile());

        User user = userService.findByUsername(principal.getName());
        landmark.setUser(user);
        landmark.setImagePath("/uploads/landmarks/" + filename);
        landmark.setCreatedAt(LocalDateTime.now());

        landmarkService.save(landmark);
        return "redirect:/users/" + principal.getName();
    }

    @GetMapping("/{id}")
    public String show(@PathVariable("id") Long id, Model model) {
        Landmark landmark = landmarkService.findById(id);
        model.addAttribute("landmark", landmark);
        model.addAttribute("comments", commentService.getCommentsForLandmark(id));
        model.addAttribute("commentForm", new LandmarkComment());
        return "landmarks/show";
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable("id") Long id, Principal principal, Model model) {
        Landmark landmark = landmarkService.findById(id);
        if (!accessControlService.canEditLandmark(landmark, principal)) {
            return "redirect:/access-denied";
        }
        model.addAttribute("landmark", landmark);
        return "landmarks/edit";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable("id") Long id,
                         @ModelAttribute("landmark") @Valid Landmark updated,
                         BindingResult result,
                         Principal principal) {
        Landmark existing = landmarkService.findById(id);
        if (!accessControlService.canEditLandmark(existing, principal)) {
            return "redirect:/access-denied";
        }
        if (result.hasErrors()) return "landmarks/edit";

        updated.setUser(existing.getUser());
        updated.setImagePath(existing.getImagePath());
        updated.setCreatedAt(existing.getCreatedAt());

        landmarkService.save(updated);
        return "redirect:/landmarks/" + id;
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable("id") Long id,
                         @RequestParam(value = "returnUrl", required = false) String returnUrl,
                         Principal principal) throws IOException {
        Landmark landmark = landmarkService.findById(id);
        if (!accessControlService.canEditLandmark(landmark, principal)) {
            return "redirect:/access-denied";
        }

        Path path = Path.of(System.getProperty("user.dir") + landmark.getImagePath());
        Files.deleteIfExists(path);
        landmarkService.delete(landmark);

        if (returnUrl != null && returnUrl.startsWith("/")) {
            return "redirect:" + returnUrl;
        }

        return "redirect:/";
    }
}