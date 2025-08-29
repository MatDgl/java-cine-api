package com.example.java_cine_api.dto.tmdb;

import java.util.List;

public class TmdbSearchResponseDto<T> {

    private Integer page;
    private List<T> results;

    @com.fasterxml.jackson.annotation.JsonProperty("total_pages")
    private Integer totalPages;

    @com.fasterxml.jackson.annotation.JsonProperty("total_results")
    private Integer totalResults;

    // Constructeurs
    public TmdbSearchResponseDto() {}

    public TmdbSearchResponseDto(Integer page, List<T> results, Integer totalPages, Integer totalResults) {
        this.page = page;
        this.results = results;
        this.totalPages = totalPages;
        this.totalResults = totalResults;
    }

    // Getters et Setters
    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public List<T> getResults() {
        return results;
    }

    public void setResults(List<T> results) {
        this.results = results;
    }

    public Integer getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }

    public Integer getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(Integer totalResults) {
        this.totalResults = totalResults;
    }
}
