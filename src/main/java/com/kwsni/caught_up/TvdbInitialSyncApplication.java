package com.kwsni.caught_up;

import java.util.Date;

import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// $ ./mvnw spring-boot:run -Dspring-boot-run.arguments=--spring.main.web-application-type=NONE

public class TvdbInitialSyncApplication implements CommandLineRunner {

    @Autowired
    private JobOperator jobOperator;

    @Autowired
    private Job tvdbUpdateSyncJob;

    @Autowired
    private Job tvdbInitialSyncJob;

    public static void main(String[] args) {
		SpringApplication.run(TvdbInitialSyncApplication.class, args);
	}
    
    @Override
    public void run(String... args) throws Exception {
        JobParameters jobParameters = new JobParametersBuilder()
            .addDate("date", new Date())
            .toJobParameters();
        JobExecution execution = jobOperator.run(tvdbInitialSyncJob, jobParameters);
        System.out.println("STATUS :: " + execution.getStatus());
    }
}
