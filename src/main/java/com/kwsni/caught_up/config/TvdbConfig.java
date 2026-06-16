package com.kwsni.caught_up.config;

import static org.springframework.http.MediaType.APPLICATION_JSON;

import java.nio.charset.StandardCharsets;
import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.http.client.ClientHttpRequestFactoryBuilder;
import org.springframework.boot.http.client.HttpClientSettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpRequest;
import org.springframework.web.client.RestClient;

import com.kwsni.caught_up.tvdb.dto.TvdbLoginRequestDto;
import com.kwsni.caught_up.tvdb.dto.TvdbLoginResponseDto;

@Configuration
public class TvdbConfig {
    private static final Logger logger = LoggerFactory.getLogger(TvdbConfig.class);
    
    private final String baseUrl;
    private final String apiKey;
    private final RedisTemplate<String, String> redisTemplate;
    

    TvdbConfig(
        @Value("${tvdb.api.base_url}") String baseUrl,
        @Value("${tvdb.api.api_key}") String apiKey,
        RedisTemplate<String, String> redisTemplate
    ) {
        this.baseUrl = baseUrl;
        this.apiKey = apiKey;
        this.redisTemplate = redisTemplate;
    }

    @Bean
    RestClient tvdbClient() {
        return RestClient.builder()
            .baseUrl(baseUrl)
            .requestFactory(ClientHttpRequestFactoryBuilder
                .httpComponents()
                .build(HttpClientSettings
                    .defaults()
                    .withConnectTimeout(Duration.ofSeconds(10))
                    .withReadTimeout(Duration.ofSeconds(30))
                )
            )
            .requestInterceptor((request, body, execution) -> {
                request.getHeaders().add("Authorization", "Bearer " + getAccessToken(baseUrl, apiKey));
                if(logger.isTraceEnabled()) {
                    logRequest(request, body);
                }
                return execution.execute(request, body);
            })
            .build();
    }

    private String getAccessToken(String baseUrl, String apiKey) {
        // If no token exists or token is expired, perform login
        String storedToken = redisTemplate.opsForValue().get("tvdbAccessToken");
        RestClient tvdbAuthenticationClient = RestClient.builder()
            .baseUrl(baseUrl)
            .build();
        if(storedToken == null) {
            TvdbLoginResponseDto loginResponse = tvdbAuthenticationClient.post()
                .uri("/login")
                .contentType(APPLICATION_JSON)
                .body(new TvdbLoginRequestDto(apiKey, null))
                .retrieve()
                .body(TvdbLoginResponseDto.class);
            String accessToken = loginResponse.data().token();
            redisTemplate.opsForValue().set("tvdbAccessToken", accessToken, Duration.ofDays(30));
            return accessToken;
        } else {
            return storedToken;
        }
    }

    private void logRequest(HttpRequest request, byte[] body) {
        logger.trace("Request: {} {}", request.getMethod(), request.getURI());
        if (body != null && body.length > 0) {
            logger.trace("Request body: {}", new String(body, StandardCharsets.UTF_8));
        }
    }
}
