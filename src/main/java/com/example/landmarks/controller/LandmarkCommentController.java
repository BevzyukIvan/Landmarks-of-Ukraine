package com.example.landmarks.controller;

import com.example.landmarks.model.Landmark;
import com.example.landmarks.model.LandmarkComment;
import com.example.landmarks.model.User;
import com.example.landmarks.security.AccessControlService;
import com.example.landmarks.service.LandmarkCommentService;
import com.example.landmarks.service.LandmarkService;
import com.example.landmarks.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@RequestMapping("/landmarks/{landmarkId}/comments")
public class LandmarkCommentController {

    private final LandmarkService landmarkService;
    private final LandmarkCommentService commentService;
    private final UserService userService;
    private final AccessControlService accessControlService;

    public LandmarkCommentController(LandmarkService landmarkService,
                                     LandmarkCommentService commentService,
                                     UserService userService,
                                     AccessControlService accessControlService) {
        this.landmarkService = landmarkService;
        this.commentService = commentService;
        this.userService = userService;
        this.accessControlService = accessControlService;
    }

    @PostMapping
    public String addComment(@PathVariable Long landmarkId,
                             @ModelAttribute("commentForm") @Valid LandmarkComment comment,
                             BindingResult result,
                             Principal principal,
                             Model model) {
        Landmark landmark = landmarkService.findById(landmarkId);

        if (result.hasErrors()) {
            model.addAttribute("landmark", landmark);
            model.addAttribute("comments", commentService.getCommentsForLandmark(landmarkId));
            return "landmarks/show";
        }

        User user = userService.findByUsername(principal.getName());
        comment.setUser(user);
        comment.setLandmark(landmark);
        commentService.save(comment);

        return "redirect:/landmarks/" + landmarkId;
    }

    @PostMapping("/{commentId}/delete")
    public String deleteComment(@PathVariable Long landmarkId,
                                @PathVariable Long commentId,
                                Principal principal) {
        LandmarkComment comment = commentService.findById(commentId);
        User currentUser = userService.findByUsername(principal.getName());

        if (!accessControlService.canEditLandmarkComment(comment, currentUser)) {
            return "redirect:/access-denied";
        }

        commentService.delete(comment);
        return "redirect:/landmarks/" + landmarkId;
    }
}