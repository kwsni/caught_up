package com.kwsni.caught_up;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.kwsni.caught_up.tvdb.service.TvdbInitialSyncJob;

// $ ./mvnw spring-boot:run -Dspring-boot-run.arguments=--spring.main.web-application-type=NONE
@SpringBootApplication
public class TvdbInitialSyncApplication implements CommandLineRunner {

    @Autowired
    private TvdbInitialSyncJob tvdbInitialSyncJob;

    public static void main(String[] args) {
		SpringApplication.run(TvdbInitialSyncApplication.class, args);
	}
    
    @Override
    public void run(String... args) {
        tvdbInitialSyncJob.initialTvdbSyncJob();
    }
}
