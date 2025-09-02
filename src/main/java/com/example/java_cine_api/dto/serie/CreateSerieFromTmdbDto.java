package com.example.java_cine_api.dto.serie;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateSerieFromTmdbDto {

    @NotNull(message = "L'ID TMDB est obligatoire")
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

    @Size(max = 255, message = "Le titre de remplacement ne peut pas dépasser 255 caractères")
    private String titleOverride;
}
