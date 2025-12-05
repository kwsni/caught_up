package com.kwsni.caught_up.tvdb.service;
import com.kwsni.caught_up.tvdb.repository.CompanyRepository;
import com.kwsni.caught_up.tvdb.repository.EpisodeRepository;
import com.kwsni.caught_up.tvdb.repository.SeasonRepository;
import com.kwsni.caught_up.tvdb.repository.SeriesRepository;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.validator.internal.util.stereotypes.Lazy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.kwsni.caught_up.tvdb.dto.CompaniesResponseDto;
import com.kwsni.caught_up.tvdb.dto.SeriesResponseDto;
import com.kwsni.caught_up.tvdb.dto.CompanyDto;
import com.kwsni.caught_up.tvdb.dto.EpisodeBaseRecordDto;
import com.kwsni.caught_up.tvdb.dto.EpisodeResponseDto;
import com.kwsni.caught_up.tvdb.dto.SeasonBaseRecordDto;
import com.kwsni.caught_up.tvdb.dto.SeriesBaseRecordDto;
import com.kwsni.caught_up.tvdb.model.Company;
import com.kwsni.caught_up.tvdb.model.Episode;
import com.kwsni.caught_up.tvdb.model.Season;
import com.kwsni.caught_up.tvdb.model.Series;


@Service
public class TvdbSyncService {
    @Autowired
    private SeriesRepository seriesRepository;

    @Autowired
    private EpisodeRepository episodeRepository;

    //@Autowired
    //private SeasonRepository seasonRepository;

    //@Autowired
    //private CompanyRepository companyRepository;

    @Autowired
    private RestClient tvdbClient;

    public void initialTvdbSync() {
        /*
        CompaniesResponseDto companiesResponse = tvdbClient.get()
            .uri("/companies")
            .retrieve()
            .body(CompaniesResponseDto.class);

        while(companiesResponse != null && companiesResponse.links().next() != null) {
            for(CompanyDto companyDto : companiesResponse.data().companies()) {
                Company company = new Company(
                    companyDto.tvdbId(),
                    companyDto.name(),
                    companyDto.country(),
                    companyDto.primaryCompanyType(),
                    companyDto.activeDate(),
                    companyDto.inactiveDate(),
                    companyDto.slug()
                );
                companyRepository.save(company);
            }
        }*/

        SeriesResponseDto seriesResponse = tvdbClient.get()
            .uri("/series")
            .retrieve()
            .body(SeriesResponseDto.class);

        while(seriesResponse.links().next() != null) {
            for(SeriesBaseRecordDto seriesDto : seriesResponse.data()) {
                List<Episode> episodes = new ArrayList<>();
                EpisodeResponseDto episodeResponse = tvdbClient.get()
                    .uri("/series/{id}/episodes/default", seriesDto.id())
                    .retrieve()
                    .body(EpisodeResponseDto.class);
                if(episodeResponse != null) {
                    for(EpisodeBaseRecordDto episodeDto : episodeResponse.data().episodes()) {
                        Episode episode = new Episode(
                            episodeDto.id(),
                            episodeDto.name(),
                            episodeDto.seasonNumber(),
                            episodeDto.seasonName(),
                            episodeDto.airsAfterSeason(),
                            episodeDto.airsBeforeEpisode(),
                            episodeDto.airsBeforeSeason(),
                            episodeDto.number(),
                            episodeDto.absoluteNumber(),
                            episodeDto.runtime(),
                            episodeDto.aired(),
                            episodeDto.year(),
                            episodeDto.image(),
                            episodeDto.imageType(),
                            episodeDto.overview(),
                            episodeDto.isMovie()
                        );
                        episodes.add(episode);
                        episodeRepository.save(episode);
                    }
                    Series series = new Series(
                        seriesDto.id(),
                        seriesDto.name(),
                        seriesDto.year(),
                        seriesDto.firstAired(),
                        seriesDto.lastAired(),
                        seriesDto.nextAired(),
                        seriesDto.score(),
                        seriesDto.image(),
                        episodes,
                        seriesDto.country(),
                        seriesDto.lastUpdated(),
                        seriesDto.slug()
                    );
                    seriesRepository.save(series);
                }
            }
            seriesResponse = tvdbClient.get()
                .uri(seriesResponse.links().next())
                .retrieve()
                .body(SeriesResponseDto.class);
        };
    }
}
