package com.example.java_cine_api.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "serie")
@Data
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Serie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @NotBlank(message = "Le titre est obligatoire")
    @Size(max = 255, message = "Le titre ne peut pas dépasser 255 caractères")
    @Column(nullable = false)
    private String title;

    @Column(name = "tmdb_id", unique = true)
    private Integer tmdbId;

    @Min(value = 0, message = "La note ne peut pas être négative")
    @Max(value = 5, message = "La note ne peut pas dépasser 5")
    @Column(columnDefinition = "REAL")
    private Float rating;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean wishlist = false;

    @Size(max = 1000, message = "La critique ne peut pas dépasser 1000 caractères")
    @Column(length = 1000)
    private String review;

    @Column(name = "view_count", nullable = false, columnDefinition = "INTEGER DEFAULT 0")
    private Integer viewCount = 0;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean watched = false;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Constructeurs personnalisés
    public Serie(String title) {
        this.title = title;
        this.wishlist = false;
        this.viewCount = 0;
        this.watched = false;
    }

    public Serie(String title, Integer tmdbId, Float rating, Boolean wishlist, String review, Integer viewCount, Boolean watched) {
        this.title = title;
        this.tmdbId = tmdbId;
        this.rating = rating;
        this.wishlist = wishlist != null ? wishlist : false;
        this.review = review;
        this.viewCount = viewCount != null ? viewCount : 0;
        this.watched = watched != null ? watched : false;
    }
}
