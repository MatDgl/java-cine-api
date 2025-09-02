package com.example.java_cine_api.dto.tmdb;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
public class TmdbMovieDto {

    private Integer id;
    private String title;
    private String overview;

    @JsonProperty("release_date")
    private String releaseDate;

    private Integer runtime;
    
    private List<GenreDto> genres;

    @JsonProperty("poster_path")
    private String posterPath;

    @JsonProperty("backdrop_path")
    private String backdropPath;

    @JsonProperty("vote_average")
    private Float voteAverage;

    @JsonProperty("vote_count")
    private Integer voteCount;

    private CreditsDto credits;

    // Classes internes pour les genres et crédits
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GenreDto {
        private Integer id;
        private String name;
    }

    @Data
    @NoArgsConstructor
    public static class CreditsDto {
        private List<CastDto> cast;
    }

    @Data
    @NoArgsConstructor
    public static class CastDto {
        private Integer id;
        private String name;
        private String character;

        @JsonProperty("profile_path")
        private String profilePath;
    }
}
