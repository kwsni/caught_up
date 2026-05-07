package com.kwsni.caught_up.social;

import java.util.TimeZone;

import org.apache.commons.rng.sampling.distribution.AliasMethodDiscreteSampler;
import org.apache.commons.rng.simple.RandomSource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.kwsni.caught_up.social.model.Member;
import com.kwsni.caught_up.social.service.MemberGenerationService;
import com.kwsni.caught_up.social.service.MemberService;
import com.kwsni.caught_up.social.service.ReviewGenerationService;
import com.kwsni.caught_up.social.service.ReviewService;
import com.kwsni.caught_up.tvdb.model.Series;
import com.kwsni.caught_up.tvdb.repository.PopularityScore;
import com.kwsni.caught_up.tvdb.service.SeriesService;

@Component
public class ScheduledGeneration {
    private final int NUM_MEMBERS_TO_GENERATE = 5;  // 5 members per day
    private final int NUM_REVIEWS_TO_GENERATE = 10; // 10 reviews overall per day
    private final int MAX_REVIEWS_PER_MEMBER = 10; // Up to 10 reviews per generated member
    private ReviewGenerationService reviewGenSvc;
    private MemberGenerationService memberGenSvc;
    private ReviewService reviewSvc;
    private MemberService memberSvc;
    private SeriesService seriesSvc;

    public ScheduledGeneration(
        ReviewGenerationService reviewGenSvc,
        MemberGenerationService memberGenSvc,
        ReviewService reviewSvc,
        MemberService memberSvc,
        SeriesService seriesSvc
    ) {
        this.reviewGenSvc = reviewGenSvc;
        this.memberGenSvc = memberGenSvc;
        this.reviewSvc = reviewSvc;
        this.memberSvc = memberSvc;
        this.seriesSvc = seriesSvc;
    }

    /*

    Generate 5 members

    Pick random generated member with uniform distribution given that they are below review quota (10 per member)
    Pick random series to review with non-uniform distribution weighed by score property (Alias method? Weighed treemap?)
    Then generate review and save, repeat 10 times
    
    */
    @Scheduled(cron = "0 0 0 * * *")
    public void generateMemberReviews() {
        // Generate members
        for(int i = 0; i < NUM_MEMBERS_TO_GENERATE; i++) {
            memberSvc.createUser(memberGenSvc.generateUser(), true);
        }

        // Generate reviews
        var members = memberSvc.sampleGeneratedMembers(NUM_REVIEWS_TO_GENERATE, MAX_REVIEWS_PER_MEMBER);
        var seriesScores = seriesSvc.getPopularSeriesScores();
        var rng = RandomSource.XO_RO_SHI_RO_128_PP.create();
        var probabilities = seriesScores.stream().mapToDouble(PopularityScore::getScore).toArray();
        var sampler = AliasMethodDiscreteSampler.of(rng, probabilities);
        for(Member member : members) {
            int sample = sampler.sample(); // index of sampled series, retrieve by precaching series id and score?
            Series series = seriesSvc.getSeries(seriesScores.get(sample).getTvdbId()).orElseThrow();
            var review = reviewGenSvc.generateReview(member, series);
            reviewSvc.saveReview(member, series, review, TimeZone.getTimeZone("UTC"), true);
        }
    }

    public void generateAndSaveReview(Member member, Series series) {
        var review = reviewGenSvc.generateReview(member, series);
        reviewSvc.saveReview(member, series, review, TimeZone.getTimeZone("UTC"), true);
    }
}
