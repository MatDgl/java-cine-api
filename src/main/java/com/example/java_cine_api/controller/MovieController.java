package com.example.java_cine_api.controller;

import com.example.java_cine_api.dto.movie.*;
import com.example.java_cine_api.entity.Movie;
import com.example.java_cine_api.service.MovieService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/movie")
public class MovieController {

    private static final Logger logger = LoggerFactory.getLogger(MovieController.class);

    private final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    /**
     * Crée un nouveau film manuellement
     */
    @PostMapping
    public ResponseEntity<Movie> create(@Valid @RequestBody CreateMovieDto createMovieDto) {
        logger.info("Requête POST /movie - Création d'un film: {}", createMovieDto.getTitle());
        Movie movie = movieService.create(createMovieDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(movie);
    }

    /**
     * Crée (ou met à jour) un film local à partir d'un ID TMDB
     */
    @PostMapping("/tmdb")
    public ResponseEntity<Movie> createFromTmdb(@Valid @RequestBody CreateMovieFromTmdbDto createFromTmdbDto) {
        logger.info("Requête POST /movie/tmdb - Création depuis TMDB ID: {}", createFromTmdbDto.getTmdbId());
        Movie movie = movieService.createFromTmdb(createFromTmdbDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(movie);
    }

    /**
     * Récupère tous les films locaux
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> findAll() {
        logger.info("Requête GET /movie - Récupération de tous les films");
        Map<String, Object> result = movieService.findAll();
        return ResponseEntity.ok(result);
    }

    /**
     * Récupère les films en wishlist
     */
    @GetMapping("/wishlist")
    public ResponseEntity<Map<String, Object>> findWishlist() {
        logger.info("Requête GET /movie/wishlist - Récupération des films en wishlist");
        Map<String, Object> result = movieService.findWishlist();
        return ResponseEntity.ok(result);
    }

    /**
     * Récupère les films notés
     */
    @GetMapping("/rated")
    public ResponseEntity<Map<String, Object>> findRated() {
        logger.info("Requête GET /movie/rated - Récupération des films notés");
        Map<String, Object> result = movieService.findRated();
        return ResponseEntity.ok(result);
    }

    /**
     * Recherche dans les films avec enrichissement TMDB
     */
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> search(
            @RequestParam(name = "q", defaultValue = "") String query,
            @RequestParam(name = "limit", defaultValue = "20") 
            @Min(value = 1, message = "La limite doit être au moins 1") 
            @Max(value = 50, message = "La limite ne peut pas dépasser 50") 
            Integer limit) {
        
        logger.info("Requête GET /movie/search - Recherche: '{}' (limite: {})", query, limit);
        
        int safeLimit = Math.max(1, Math.min(50, limit));
        Map<String, Object> result = movieService.search(query, safeLimit);
        
        return ResponseEntity.ok(result);
    }

    /**
     * Récupère les détails TMDB d'un film avec statut local
     */
    @GetMapping("/tmdb/{tmdbId}")
    public ResponseEntity<Map<String, Object>> getTmdbMovie(@PathVariable Integer tmdbId) {
        logger.info("Requête GET /movie/tmdb/{} - Récupération des détails TMDB", tmdbId);
        Map<String, Object> result = movieService.findByTmdbIdWithTmdbDetails(tmdbId);
        return ResponseEntity.ok(result);
    }

    /**
     * Récupère un film local par son ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> findOne(@PathVariable Long id) {
        logger.info("Requête GET /movie/{} - Récupération du film", id);
        Map<String, Object> result = movieService.findOne(id);
        return ResponseEntity.ok(result);
    }

    /**
     * Met à jour un film local
     */
    @PutMapping("/{id}")
    public ResponseEntity<Movie> update(@PathVariable Long id, @Valid @RequestBody UpdateMovieDto updateMovieDto) {
        logger.info("Requête PUT /movie/{} - Mise à jour du film", id);
        Movie movie = movieService.update(id, updateMovieDto);
        return ResponseEntity.ok(movie);
    }

    /**
     * Supprime un film local
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Movie> remove(@PathVariable Long id) {
        logger.info("Requête DELETE /movie/{} - Suppression du film", id);
        Movie movie = movieService.remove(id);
        return ResponseEntity.ok(movie);
    }
}
