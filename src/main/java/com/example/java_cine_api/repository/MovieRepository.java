package com.example.java_cine_api.repository;

import com.example.java_cine_api.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {

    /**
     * Trouve un film par son ID TMDB
     */
    Optional<Movie> findByTmdbId(Integer tmdbId);

    /**
     * Vérifie si un film existe avec cet ID TMDB
     */
    boolean existsByTmdbId(Integer tmdbId);

    /**
     * Trouve tous les films en wishlist
     */
    List<Movie> findByWishlistTrue();

    /**
     * Trouve tous les films notés (rating non null)
     */
    @Query("SELECT m FROM Movie m WHERE m.rating IS NOT NULL")
    List<Movie> findByRatingIsNotNull();

    /**
     * Trouve tous les films regardés
     */
    List<Movie> findByWatchedTrue();

    /**
     * Trouve des films par liste d'IDs TMDB
     */
    @Query("SELECT m FROM Movie m WHERE m.tmdbId IN :tmdbIds")
    List<Movie> findByTmdbIdIn(@Param("tmdbIds") List<Integer> tmdbIds);

    /**
     * Recherche dans les titres (insensible à la casse)
     */
    @Query("SELECT m FROM Movie m WHERE LOWER(m.title) LIKE LOWER(CONCAT('%', :title, '%'))")
    List<Movie> findByTitleContainingIgnoreCase(@Param("title") String title);

    /**
     * Trouve les films triés par date de création descendante
     */
    List<Movie> findAllByOrderByCreatedAtDesc();
}
