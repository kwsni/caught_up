package com.kwsni.caught_up.tvdb.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.kwsni.caught_up.tvdb.repository.SeriesRepository;

@Controller
@RequestMapping("/series")
public class SeriesController {

    @Autowired
    private SeriesRepository seriesRepository;

    @GetMapping
    public String listSeries(Model model) {
        model.addAttribute("series", seriesRepository.findAll());
        return "series";
    }
}
