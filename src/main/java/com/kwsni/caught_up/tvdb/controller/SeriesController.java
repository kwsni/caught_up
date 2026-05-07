package com.kwsni.caught_up.tvdb.controller;

import java.security.Principal;
import java.time.LocalDate;
import java.util.TimeZone;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.kwsni.caught_up.social.controller.dto.PostReviewDto;
import com.kwsni.caught_up.social.model.Member;
import com.kwsni.caught_up.social.model.Review;
import com.kwsni.caught_up.social.service.MemberService;
import com.kwsni.caught_up.social.service.ReviewService;
import com.kwsni.caught_up.tvdb.model.Series;
import com.kwsni.caught_up.tvdb.service.EpisodeService;
import com.kwsni.caught_up.tvdb.service.SeriesService;

@Controller
@RequestMapping("/series")
public class SeriesController {
    private final SeriesService seriesSvc;
    private final EpisodeService episodeSvc;
    private final ReviewService reviewSvc;
    private final MemberService memberSvc;

    public SeriesController(
        SeriesService seriesSvc,
        EpisodeService episodeSvc,
        ReviewService reviewSvc,
        MemberService memberSvc
    ) {
        this.seriesSvc = seriesSvc;
        this.episodeSvc = episodeSvc;
        this.reviewSvc = reviewSvc;
        this.memberSvc = memberSvc;
    }
    @GetMapping
    public String homeSeries(Model model) {
        var popularSeries = seriesSvc.getPopularSeries();
        var justReviewed = reviewSvc.getRecentReviews();
        
        model.addAttribute("popularSeries", popularSeries);
        model.addAttribute("justReviewed", justReviewed);

        return "series-home";
    }

    @GetMapping("/browse")
    public String listSeries(
        @RequestParam(defaultValue="score,desc") String[] sort,
        @RequestParam(defaultValue="sm") String size,
        @RequestParam(defaultValue="0") int page,
        Model model
    ) {
        var seriesPage = seriesSvc.getSortedSeriesPage(sort, size, page);

        model.addAttribute("seriesList", seriesPage);
        model.addAttribute("isLarge", size.equals("lg"));
        model.addAttribute("prevPage", page - 1);
        model.addAttribute("nextPage", page + 1);
        model.addAttribute("hasPrev", seriesPage.hasPrevious());
        model.addAttribute("hasNext", seriesPage.hasNext());

        return "series-list";
    }

    @GetMapping("/{slug}")
    public String showSeries(@PathVariable("slug") String slug, Model model) {
        var series = seriesSvc.getSeries(slug).orElseThrow();
        var episodesBySeason = episodeSvc.getEpisodesGroupedBySeason(series);
        var popularReviews = reviewSvc.getPopularReviews(slug);
        var avgRating = reviewSvc.avgRating(slug);
        
        // Remove non-regular episodes
        episodesBySeason.remove(0);

        model.addAttribute("series", series);
        model.addAttribute("episodesBySeason", episodesBySeason);
        model.addAttribute("reviews", popularReviews);
        model.addAttribute("avgRating", avgRating);
        model.addAttribute("postReview", new PostReviewDto(
            "",
            LocalDate.now(),
            0.0,
            false,
            false
        ));

        return "series";
    }

    @PostMapping("/{slug}")
    public String postReview(
        @ModelAttribute PostReviewDto postReview,
        @PathVariable("slug") String slug,
        TimeZone timezone,
        Principal principal,
        Model model) {
            Member author = memberSvc.getMemberByUsername(principal.getName()); // TODO: get principal securely
            Series series = seriesSvc.getSeries(slug).orElseThrow();
            
            var reviewId = reviewSvc.saveReview(author, series, postReview, timezone, false);

            return "redirect:/reviews/" + reviewId;
    }

    @GetMapping("/{slug}/reviews")
    public String showSeriesReviews(
        @PathVariable("slug") String slug,
        @RequestParam(required=false) Double rating, 
        @RequestParam(defaultValue="createdDate,desc") String[] sort,
        @RequestParam(defaultValue="0") int page,
        Model model
    ) {
        Page<Review> reviewsList = reviewSvc.getSeriesReviewList(slug, rating, sort, page);
        Series series = seriesSvc.getSeries(slug).orElseThrow();

        model.addAttribute("series", series);
        model.addAttribute("reviews", reviewsList);
        model.addAttribute("isBrowse", true);
        model.addAttribute("prevPage", page - 1);
        model.addAttribute("nextPage", page + 1);
        model.addAttribute("hasPrev", reviewsList.hasPrevious());
        model.addAttribute("hasNext", reviewsList.hasNext());

        return "series-reviews";
    }
}
