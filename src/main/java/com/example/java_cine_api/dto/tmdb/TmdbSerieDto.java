package com.example.java_cine_api.dto.tmdb;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class TmdbSerieDto {

    private Integer id;
    private String name;
    private String overview;

    @JsonProperty("first_air_date")
    private String firstAirDate;

    @JsonProperty("number_of_seasons")
    private Integer numberOfSeasons;

    @JsonProperty("number_of_episodes")
    private Integer numberOfEpisodes;

    @JsonProperty("episode_run_time")
    private List<Integer> episodeRunTime;

    private List<TmdbMovieDto.GenreDto> genres;

    @JsonProperty("poster_path")
    private String posterPath;

    @JsonProperty("backdrop_path")
    private String backdropPath;

    @JsonProperty("vote_average")
    private Float voteAverage;

    @JsonProperty("vote_count")
    private Integer voteCount;

    @JsonProperty("created_by")
    private List<CreatorDto> createdBy;

    private TmdbMovieDto.CreditsDto credits;

    // Classe interne pour les créateurs
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreatorDto {
        private Integer id;
        private String name;
    }
}
