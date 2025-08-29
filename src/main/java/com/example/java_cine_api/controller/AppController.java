package com.example.java_cine_api.controller;

import com.example.java_cine_api.service.TmdbService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@RestController
@RequestMapping("/")
public class AppController {

    private static final Logger logger = LoggerFactory.getLogger(AppController.class);

    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final TmdbService tmdbService;

    public AppController(TmdbService tmdbService) {
        this.tmdbService = tmdbService;
    }

    /**
     * Point d'entrée de l'API - Information de santé
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getInfo() {
        logger.info("Requête GET / - Information de l'API");

        Map<String, Object> info = Map.of(
                "name", "Java Cine API",
                "description", "API de gestion de films et séries avec intégration TMDB",
                "timestamp", LocalDateTime.now().format(TIMESTAMP_FORMATTER),
                "status", "running");

        return ResponseEntity.ok(info);
    }

    /**
     * Recherche multi (films + séries) dans TMDB
     */
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchMulti(
            @RequestParam(name = "q", defaultValue = "") String query,
            @RequestParam(name = "limit", defaultValue = "20") Integer limit) {

        if (query.trim().isEmpty()) {
            return ResponseEntity.ok(Map.of(
                    "query", query,
                    "limit", limit,
                    "total", 0,
                    "results", java.util.List.of()));
        }

        logger.info("Requête GET /search - Recherche multi: '{}' (limite: {})", query, limit);

        int safeLimit = Math.max(1, Math.min(50, limit));
        var tmdbResponse = tmdbService.searchMulti(query);

        if (tmdbResponse == null || tmdbResponse.getResults() == null) {
            return ResponseEntity.ok(Map.of(
                    "query", query,
                    "limit", safeLimit,
                    "total", 0,
                    "results", java.util.List.of()));
        }

        var limitedResults = tmdbResponse.getResults().stream()
                .limit(safeLimit)
                .toList();

        return ResponseEntity.ok(Map.of(
                "query", query,
                "limit", safeLimit,
                "total", limitedResults.size(),
                "results", limitedResults));
    }

}
