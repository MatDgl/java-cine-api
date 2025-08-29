package com.example.java_cine_api.repository;

import com.example.java_cine_api.entity.Serie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SerieRepository extends JpaRepository<Serie, Long> {

    /**
     * Trouve une série par son ID TMDB
     */
    Optional<Serie> findByTmdbId(Integer tmdbId);

    /**
     * Vérifie si une série existe avec cet ID TMDB
     */
    boolean existsByTmdbId(Integer tmdbId);

    /**
     * Trouve toutes les séries en wishlist
     */
    List<Serie> findByWishlistTrue();

    /**
     * Trouve toutes les séries notées (rating non null)
     */
    @Query("SELECT s FROM Serie s WHERE s.rating IS NOT NULL")
    List<Serie> findByRatingIsNotNull();

    /**
     * Trouve toutes les séries regardées
     */
    List<Serie> findByWatchedTrue();

    /**
     * Trouve des séries par liste d'IDs TMDB
     */
    @Query("SELECT s FROM Serie s WHERE s.tmdbId IN :tmdbIds")
    List<Serie> findByTmdbIdIn(@Param("tmdbIds") List<Integer> tmdbIds);

    /**
     * Recherche dans les titres (insensible à la casse)
     */
    @Query("SELECT s FROM Serie s WHERE LOWER(s.title) LIKE LOWER(CONCAT('%', :title, '%'))")
    List<Serie> findByTitleContainingIgnoreCase(@Param("title") String title);

    /**
     * Trouve les séries triées par date de création descendante
     */
    List<Serie> findAllByOrderByCreatedAtDesc();
}
