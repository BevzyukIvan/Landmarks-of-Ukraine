package com.example.landmarks.controller;

import com.example.landmarks.model.Landmark;
import com.example.landmarks.service.LandmarkService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/landmarks")
public class LandmarkController {
    private final LandmarkService service;

    @Autowired
    public LandmarkController(LandmarkService service) {
        this.service = service;
    }

    @GetMapping()
    public String index(Model model) {
        model.addAttribute("landmarks", service.findAll());
        return "landmarks/index";
    }

    @GetMapping("/{id}")
    public String show(@PathVariable("id") Long id, Model model) {
        model.addAttribute("landmark", service.findById(id));
        return "landmarks/show";
    }

    @GetMapping("/new")
    public String newLandmark(@ModelAttribute("landmark") Landmark landmark) {
        return "landmarks/new";
    }

    @PostMapping()
    public String create(@ModelAttribute("landmark") @Valid Landmark landmark, BindingResult result) {
        if (result.hasErrors()) return "landmarks/new";

        service.save(landmark);
        return "redirect:/landmarks";
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable("id") Long id, Model model) {
        model.addAttribute("landmark", service.findById(id));
        return "landmarks/edit";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable("id") Long id, @ModelAttribute("landmark") @Valid Landmark landmark, BindingResult result) {
        if (result.hasErrors()) return "landmarks/edit";

        landmark.setId(id);
        service.save(landmark);
        return "redirect:/landmarks";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable("id") Long id) {
        service.delete(id);
        return "redirect:/landmarks";
    }
}

