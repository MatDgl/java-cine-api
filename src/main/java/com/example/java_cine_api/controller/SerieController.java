package com.example.java_cine_api.controller;

import com.example.java_cine_api.dto.serie.*;
import com.example.java_cine_api.entity.Serie;
import com.example.java_cine_api.service.SerieService;
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
@RequestMapping("/serie")
public class SerieController {

    private static final Logger logger = LoggerFactory.getLogger(SerieController.class);

    private final SerieService serieService;

    public SerieController(SerieService serieService) {
        this.serieService = serieService;
    }

    /**
     * Crée une nouvelle série manuellement
     */
    @PostMapping
    public ResponseEntity<Serie> create(@Valid @RequestBody CreateSerieDto createSerieDto) {
        logger.info("Requête POST /serie - Création d'une série: {}", createSerieDto.getTitle());
        Serie serie = serieService.create(createSerieDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(serie);
    }

    /**
     * Crée (ou met à jour) une série locale à partir d'un ID TMDB
     */
    @PostMapping("/tmdb")
    public ResponseEntity<Serie> createFromTmdb(@Valid @RequestBody CreateSerieFromTmdbDto createFromTmdbDto) {
        logger.info("Requête POST /serie/tmdb - Création depuis TMDB ID: {}", createFromTmdbDto.getTmdbId());
        Serie serie = serieService.createFromTmdb(createFromTmdbDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(serie);
    }

    /**
     * Récupère toutes les séries locales
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> findAll() {
        logger.info("Requête GET /serie - Récupération de toutes les séries");
        Map<String, Object> result = serieService.findAll();
        return ResponseEntity.ok(result);
    }

    /**
     * Récupère les séries en wishlist
     */
    @GetMapping("/wishlist")
    public ResponseEntity<Map<String, Object>> findWishlist() {
        logger.info("Requête GET /serie/wishlist - Récupération des séries en wishlist");
        Map<String, Object> result = serieService.findWishlist();
        return ResponseEntity.ok(result);
    }

    /**
     * Récupère les séries notées
     */
    @GetMapping("/rated")
    public ResponseEntity<Map<String, Object>> findRated() {
        logger.info("Requête GET /serie/rated - Récupération des séries notées");
        Map<String, Object> result = serieService.findRated();
        return ResponseEntity.ok(result);
    }

    /**
     * Recherche dans les séries avec enrichissement TMDB
     */
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> search(
            @RequestParam(name = "q", defaultValue = "") String query,
            @RequestParam(name = "limit", defaultValue = "20") 
            @Min(value = 1, message = "La limite doit être au moins 1") 
            @Max(value = 50, message = "La limite ne peut pas dépasser 50") 
            Integer limit) {
        
        logger.info("Requête GET /serie/search - Recherche: '{}' (limite: {})", query, limit);
        
        int safeLimit = Math.max(1, Math.min(50, limit));
        Map<String, Object> result = serieService.search(query, safeLimit);
        
        return ResponseEntity.ok(result);
    }

    /**
     * Récupère les détails TMDB d'une série avec statut local
     */
    @GetMapping("/tmdb/{tmdbId}")
    public ResponseEntity<Map<String, Object>> getTmdbSerie(@PathVariable Integer tmdbId) {
        logger.info("Requête GET /serie/tmdb/{} - Récupération des détails TMDB", tmdbId);
        Map<String, Object> result = serieService.findByTmdbIdWithTmdbDetails(tmdbId);
        return ResponseEntity.ok(result);
    }

    /**
     * Récupère une série locale par son ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> findOne(@PathVariable Long id) {
        logger.info("Requête GET /serie/{} - Récupération de la série", id);
        Map<String, Object> result = serieService.findOne(id);
        return ResponseEntity.ok(result);
    }

    /**
     * Met à jour une série locale
     */
    @PutMapping("/{id}")
    public ResponseEntity<Serie> update(@PathVariable Long id, @Valid @RequestBody UpdateSerieDto updateSerieDto) {
        logger.info("Requête PUT /serie/{} - Mise à jour de la série", id);
        Serie serie = serieService.update(id, updateSerieDto);
        return ResponseEntity.ok(serie);
    }

    /**
     * Supprime une série locale
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Serie> remove(@PathVariable Long id) {
        logger.info("Requête DELETE /serie/{} - Suppression de la série", id);
        Serie serie = serieService.remove(id);
        return ResponseEntity.ok(serie);
    }
}
