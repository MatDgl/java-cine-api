package com.example.java_cine_api.dto.serie;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CreateSerieDto {

    @NotBlank(message = "Le titre est obligatoire")
    @Size(max = 255, message = "Le titre ne peut pas dépasser 255 caractères")
    private String title;

    private Integer tmdbId;

    @Min(value = 0, message = "La note ne peut pas être négative")
    @Max(value = 5, message = "La note ne peut pas dépasser 5")
    private Float rating;

    private Boolean wishlist;

    @Size(max = 1000, message = "La critique ne peut pas dépasser 1000 caractères")
    private String review;

    @Min(value = 0, message = "Le nombre de vues ne peut pas être négatif")
    private Integer viewCount;

    private Boolean watched;

    // Constructeurs
    public CreateSerieDto() {}

    public CreateSerieDto(String title, Integer tmdbId, Float rating, Boolean wishlist, String review, Integer viewCount, Boolean watched) {
        this.title = title;
        this.tmdbId = tmdbId;
        this.rating = rating;
        this.wishlist = wishlist;
        this.review = review;
        this.viewCount = viewCount;
        this.watched = watched;
    }

    // Getters et Setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getTmdbId() {
        return tmdbId;
    }

    public void setTmdbId(Integer tmdbId) {
        this.tmdbId = tmdbId;
    }

    public Float getRating() {
        return rating;
    }

    public void setRating(Float rating) {
        this.rating = rating;
    }

    public Boolean getWishlist() {
        return wishlist;
    }

    public void setWishlist(Boolean wishlist) {
        this.wishlist = wishlist;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public Integer getViewCount() {
        return viewCount;
    }

    public void setViewCount(Integer viewCount) {
        this.viewCount = viewCount;
    }

    public Boolean getWatched() {
        return watched;
    }

    public void setWatched(Boolean watched) {
        this.watched = watched;
    }
}
