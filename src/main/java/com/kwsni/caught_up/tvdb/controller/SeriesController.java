package com.kwsni.caught_up.tvdb.controller;

import java.security.Principal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.kwsni.caught_up.social.dto.PostReviewDto;
import com.kwsni.caught_up.social.model.Member;
import com.kwsni.caught_up.social.model.Review;
import com.kwsni.caught_up.social.repository.MemberRepository;
import com.kwsni.caught_up.social.repository.ReviewRepository;
import com.kwsni.caught_up.tvdb.model.Episode;
import com.kwsni.caught_up.tvdb.model.Series;
import com.kwsni.caught_up.tvdb.repository.EpisodeRepository;
import com.kwsni.caught_up.tvdb.repository.SeriesRepository;

@Controller
@RequestMapping("/series")
public class SeriesController {

    @Autowired
    private SeriesRepository seriesRepository;

    @Autowired
    private EpisodeRepository episodeRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private MemberRepository memberRepository;

    @GetMapping
    public String homeSeries(Model model) {
        Pageable sortedByPopularityDesc = PageRequest.of(0, 4, Sort.by("score").descending());
        Page<Series> popularSeriesList = seriesRepository.findAll(sortedByPopularityDesc);

        Pageable sortedByDateDesc = PageRequest.of(0, 12, Sort.by("createdDate").descending());
        Page<Review> justReviewedList = reviewRepository.findAll(sortedByDateDesc);

        model.addAttribute("popularSeries", popularSeriesList);
        model.addAttribute("justReviewed", justReviewedList);

        return "series-home";
    }

    @GetMapping("/browse")
    public String listSeries(
        @RequestParam(defaultValue="score,desc") String[] sort,
        @RequestParam(defaultValue="sm") String size,
        @RequestParam(defaultValue="0") int page,
        Model model
    ) {
        String sortField = sort[0];
        String sortDir = sort[1];

        Direction dir = sortDir.equals("desc") ? Direction.DESC : Direction.ASC;
        boolean isLarge = size.equals("lg");
        int pageSize = isLarge ? 18 : 72;
        
        Pageable sortBy = PageRequest.of(page, pageSize, Sort.by(Sort.Order.by(sortField).with(dir).nullsLast()));
        Page<Series> seriesList = seriesRepository.findAll(sortBy);

        model.addAttribute("seriesList", seriesList);
        model.addAttribute("isLarge", isLarge);
        model.addAttribute("prevPage", page - 1);
        model.addAttribute("nextPage", page + 1);
        model.addAttribute("hasPrev", seriesList.hasPrevious());
        model.addAttribute("hasNext", seriesList.hasNext());

        return "series-list";
    }

    @GetMapping("/{slug}")
    public String showSeries(@PathVariable("slug") String slug, Model model) {
        Series series = seriesRepository.findBySlug(slug).get();
        Map<Integer, List<Episode>> episodesBySeason = episodeRepository.findBySeries(series, Sort.by("seasonNumber", "number").ascending())
            .stream().collect(Collectors.groupingBy(Episode::getSeasonNumber));
        //TODO: SORT BY LIKES SIZE USING JPQL?
        Page<Review> popularReviews = reviewRepository.findBySeries_Slug(slug, PageRequest.of(0, 3, Sort.by("id").descending()));
        Double avgRating = reviewRepository.avgRatingsBySeries_Slug(slug);

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
            Member author = memberRepository.findByUsername(principal.getName());
            Series series = seriesRepository.findBySlug(slug).get();
            ZoneId tz = timezone.toZoneId();

            Review newReview = new Review(
                author,
                series,
                postReview.content(),
                postReview.watchedOn() != null ? postReview.watchedOn().atStartOfDay(tz).toOffsetDateTime() : null,
                postReview.rating(),
                postReview.isSpoiler(),
                postReview.like()
            );

            newReview = reviewRepository.save(newReview);

            return "redirect:/reviews/" + newReview.getId();
    }

    @GetMapping("/{slug}/reviews")
    public String showSeriesReviews(
        @PathVariable("slug") String slug,
        @RequestParam(required=false) Double rating, 
        @RequestParam(defaultValue="createdDate,desc") String[] sort,
        @RequestParam(defaultValue="0") int page,
        Model model
    ) {
        String sortField = sort[0];
        String sortDir = sort[1];

        Direction dir = sortDir.equals("desc") ? Direction.DESC : Direction.ASC;

        Pageable sortBy = PageRequest.of(page, 12, Sort.by(Sort.Order.by(sortField).with(dir).nullsLast()));
        Page<Review> reviewsList;
        if(rating != null) {
            reviewsList = reviewRepository.findBySeries_SlugAndRating(slug, rating, sortBy);
        } else {
            reviewsList = reviewRepository.findBySeries_Slug(slug, sortBy);
        }

        Series series = seriesRepository.findBySlug(slug).get();


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
