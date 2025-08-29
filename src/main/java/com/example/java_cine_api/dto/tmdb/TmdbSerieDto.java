package com.example.java_cine_api.dto.tmdb;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

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

    // Constructeurs
    public TmdbSerieDto() {}

    // Getters et Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getFirstAirDate() {
        return firstAirDate;
    }

    public void setFirstAirDate(String firstAirDate) {
        this.firstAirDate = firstAirDate;
    }

    public Integer getNumberOfSeasons() {
        return numberOfSeasons;
    }

    public void setNumberOfSeasons(Integer numberOfSeasons) {
        this.numberOfSeasons = numberOfSeasons;
    }

    public Integer getNumberOfEpisodes() {
        return numberOfEpisodes;
    }

    public void setNumberOfEpisodes(Integer numberOfEpisodes) {
        this.numberOfEpisodes = numberOfEpisodes;
    }

    public List<Integer> getEpisodeRunTime() {
        return episodeRunTime;
    }

    public void setEpisodeRunTime(List<Integer> episodeRunTime) {
        this.episodeRunTime = episodeRunTime;
    }

    public List<TmdbMovieDto.GenreDto> getGenres() {
        return genres;
    }

    public void setGenres(List<TmdbMovieDto.GenreDto> genres) {
        this.genres = genres;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
    }

    public Float getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(Float voteAverage) {
        this.voteAverage = voteAverage;
    }

    public Integer getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(Integer voteCount) {
        this.voteCount = voteCount;
    }

    public List<CreatorDto> getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(List<CreatorDto> createdBy) {
        this.createdBy = createdBy;
    }

    public TmdbMovieDto.CreditsDto getCredits() {
        return credits;
    }

    public void setCredits(TmdbMovieDto.CreditsDto credits) {
        this.credits = credits;
    }

    // Classe interne pour les créateurs
    public static class CreatorDto {
        private Integer id;
        private String name;

        public CreatorDto() {}

        public CreatorDto(Integer id, String name) {
            this.id = id;
            this.name = name;
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
