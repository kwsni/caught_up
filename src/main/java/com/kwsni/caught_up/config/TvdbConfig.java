package com.kwsni.caught_up.config;

import static org.springframework.http.MediaType.APPLICATION_JSON;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RestClient;

import com.kwsni.caught_up.tvdb.dto.TvdbLoginRequestDto;
import com.kwsni.caught_up.tvdb.dto.TvdbLoginResponseDto;
import jakarta.annotation.PostConstruct;

@Configuration
public class TvdbConfig {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Bean
    RestClient tvdbClient(@Value("${tvdb.api.base_url}") String baseUrl, @Value("${tvdb.api.api_key}") String apiKey) {
        return RestClient.builder()
            .baseUrl(baseUrl)
            .requestInterceptor((request, body, execution) -> {
                request.getHeaders().add("Authorization", "Bearer " + getAccessToken(baseUrl, apiKey));
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
}
