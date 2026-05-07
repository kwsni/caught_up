package com.kwsni.caught_up.social.service;

import java.time.Instant;

import org.springframework.ai.chat.client.AdvisorParams;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

import com.kwsni.caught_up.social.controller.dto.PostReviewDto;
import com.kwsni.caught_up.social.model.Member;
import com.kwsni.caught_up.tvdb.model.Series;

@Service
public class ReviewGenerationService {
    private final ChatClient chatClient;

    public ReviewGenerationService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    public PostReviewDto generateReview(Member member, Series series) {
        return chatClient.prompt()
            .advisors(AdvisorParams.ENABLE_NATIVE_STRUCTURED_OUTPUT)
            .user(u -> u.text("You are a user named " + member.getUsername() + ". Write a review of the TV series " + series.getName() + "(" + series.getYear() + ") in the style of a typical Letterboxd review. Give a rating and/or a like appropriate for the written content, and flag for spoilers if necessary. Make sure the review is dated on or before the current date.")
                .metadata("userId", member.getId()))
            .call()
            .entity(PostReviewDto.class);
    }

    class DateTimeTools {
        @Tool(description = "Get the current date and time in UTC timezone")
        String getCurrentDateTime() {
            return Instant.now().toString();
        }
    }
}
