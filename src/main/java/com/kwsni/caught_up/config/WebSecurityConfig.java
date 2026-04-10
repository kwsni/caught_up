package com.kwsni.caught_up.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

  @Bean
  SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    String[] authenticatedPaths = {"/settings", "/**/follow", "/**/unfollow", "/**/like", "/**/unlike"};
    http
      .authorizeHttpRequests((requests) -> requests
        .requestMatchers(authenticatedPaths).authenticated()
        .anyRequest().permitAll()
      )
      .formLogin((form) -> form
        .loginPage("/sign-in")
        .defaultSuccessUrl("/series")
        .permitAll()
      )
      .logout((logout) -> logout
        .permitAll()
        .logoutRequestMatcher(PathPatternRequestMatcher
          .withDefaults()
          .matcher(HttpMethod.GET, "/logout"))
          .logoutSuccessUrl("/?logout")
      );
    return http.build();
  }

  @Bean
  PasswordEncoder passwordEncoder() {
    return PasswordEncoderFactories.createDelegatingPasswordEncoder();
  }
}
