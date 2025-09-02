package com.example.java_cine_api.service;

import com.example.java_cine_api.dto.serie.*;
import com.example.java_cine_api.dto.tmdb.TmdbSerieDto;
import com.example.java_cine_api.entity.Serie;
import com.example.java_cine_api.exception.ResourceNotFoundException;
import com.example.java_cine_api.repository.SerieRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@Transactional
public class SerieService {

    private static final Logger logger = LoggerFactory.getLogger(SerieService.class);

    private final SerieRepository serieRepository;
    private final TmdbService tmdbService;

    public SerieService(SerieRepository serieRepository, TmdbService tmdbService) {
        this.serieRepository = serieRepository;
        this.tmdbService = tmdbService;
    }

    /**
     * Crée une nouvelle série
     */
    public Serie create(CreateSerieDto dto) {
        logger.info("Création d'une nouvelle série: {}", dto.getTitle());
        
        Serie serie = new Serie(
            dto.getTitle(),
            dto.getTmdbId(),
            dto.getRating(),
            dto.getWishlist(),
            dto.getReview(),
            dto.getViewCount(),
            dto.getWatched()
        );
        
        Serie savedSerie = serieRepository.save(serie);
        logger.debug("Série créée avec l'ID: {}", savedSerie.getId());
        return savedSerie;
    }

    /**
     * Crée (ou met à jour) une série locale à partir d'un tmdbId. Si une série avec ce tmdbId
     * existe déjà on met simplement à jour les champs utilisateurs fournis.
     */
    public Serie createFromTmdb(CreateSerieFromTmdbDto dto) {
        logger.info("Création/mise à jour d'une série depuis TMDB ID: {}", dto.getTmdbId());
        
        // Récupérer les détails depuis TMDB
        TmdbSerieDto tmdbSerie = tmdbService.getSerieDetails(dto.getTmdbId());
        
        // Vérifier si la série existe déjà localement
        Serie existingSerie = serieRepository.findByTmdbId(dto.getTmdbId()).orElse(null);
        
        if (existingSerie != null) {
            // Mettre à jour la série existante avec les nouvelles données utilisateur
            logger.debug("Mise à jour de la série existante: {}", existingSerie.getTitle());
            updateSerieFromDto(existingSerie, dto, tmdbSerie);
            return serieRepository.save(existingSerie);
        } else {
            // Créer une nouvelle série
            String title = StringUtils.hasText(dto.getTitleOverride()) 
                ? dto.getTitleOverride() 
                : tmdbSerie.getName();
                
            Serie newSerie = new Serie(
                title,
                dto.getTmdbId(),
                dto.getRating(),
                dto.getWishlist(),
                dto.getReview(),
                dto.getViewCount(),
                dto.getWatched()
            );
            
            Serie savedSerie = serieRepository.save(newSerie);
            logger.debug("Nouvelle série créée depuis TMDB: {}", savedSerie.getTitle());
            return savedSerie;
        }
    }

    /**
     * Récupère toutes les séries avec enrichissement du poster_path
     */
    public Map<String, Object> findAll() {
        logger.info("Récupération de toutes les séries");
        
        List<Serie> series = serieRepository.findAllByOrderByCreatedAtDesc();
        List<Map<String, Object>> enrichedSeries = enrichPosterPath(series);
        
        Map<String, Object> result = new HashMap<>();
        result.put("items", enrichedSeries);
        return result;
    }

    /**
     * Récupère toutes les séries en wishlist avec enrichissement du poster_path
     */
    public Map<String, Object> findWishlist() {
        logger.info("Récupération des séries en wishlist");
        
        List<Serie> series = serieRepository.findByWishlistTrue();
        List<Map<String, Object>> enrichedSeries = enrichPosterPath(series);
        
        Map<String, Object> result = new HashMap<>();
        result.put("items", enrichedSeries);
        return result;
    }

    /**
     * Récupère toutes les séries notées avec enrichissement du poster_path
     */
    public Map<String, Object> findRated() {
        logger.info("Récupération des séries notées");
        List<Serie> series = serieRepository.findByRatingIsNotNull();
        List<Map<String, Object>> enriched = enrichPosterPath(series);
        
        Map<String, Object> result = new HashMap<>();
        result.put("items", enriched);
        result.put("total", enriched.size());
        return result;
    }

    /**
     * Récupère une série par son ID local
     */
    @Transactional(readOnly = true)
    public Map<String, Object> findOne(Long id) {
        logger.info("Récupération de la série avec ID: {}", id);
        
        Serie serie = serieRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Série", id));
        
        Map<String, Object> result = convertSerieToMap(serie);
        
        // Enrichir avec les données TMDB si disponibles
        if (serie.getTmdbId() != null) {
            try {
                TmdbSerieDto tmdbSerie = tmdbService.getSerieDetails(serie.getTmdbId());
                result.put("tmdb", tmdbSerie);
            } catch (Exception e) {
                logger.warn("Impossible de récupérer les détails TMDB pour la série {}: {}", 
                    serie.getId(), e.getMessage());
            }
        }
        
        return result;
    }

    /**
     * Récupère les détails TMDB et l'éventuel enregistrement local via tmdbId.
     * Le champ `local` vaut `null` si l'élément n'existe pas en base.
     */
    @Transactional(readOnly = true)
    public Map<String, Object> findByTmdbIdWithTmdbDetails(Integer tmdbId) {
        logger.info("Récupération de la série via TMDB ID: {}", tmdbId);
        
        TmdbSerieDto tmdbSerie = tmdbService.getSerieDetails(tmdbId);
        Serie localSerie = serieRepository.findByTmdbId(tmdbId).orElse(null);
        
        Map<String, Object> result = new HashMap<>();
        result.put("tmdb", tmdbSerie);
        result.put("local", localSerie);
        return result;
    }

    /**
     * Met à jour une série
     */
    public Serie update(Long id, UpdateSerieDto dto) {
        logger.info("Mise à jour de la série avec ID: {}", id);
        
        Serie serie = serieRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Série", id));
        
        updateSerieFromUpdateDto(serie, dto);
        
        Serie updatedSerie = serieRepository.save(serie);
        logger.debug("Série mise à jour: {}", updatedSerie.getTitle());
        return updatedSerie;
    }

    /**
     * Supprime une série
     */
    public Serie remove(Long id) {
        logger.info("Suppression de la série avec ID: {}", id);
        
        Serie serie = serieRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Série", id));
        
        serieRepository.delete(serie);
        logger.debug("Série supprimée: {}", serie.getTitle());
        return serie;
    }

    /**
     * Recherche des séries via TMDB et indique si déjà présent localement
     */
    @Transactional(readOnly = true)
    public Map<String, Object> search(String query, int limit) {
        String trimmedQuery = query != null ? query.trim() : "";
        
        if (trimmedQuery.isEmpty()) {
            return Map.of(
                "query", trimmedQuery,
                "limit", limit,
                "total", 0,
                "results", List.of()
            );
        }
        
        logger.info("Recherche de séries pour la requête: {} (limite: {})", trimmedQuery, limit);
        
        var tmdbResponse = tmdbService.searchSeries(trimmedQuery);
        
        if (tmdbResponse == null || tmdbResponse.getResults() == null) {
            return Map.of(
                "query", trimmedQuery,
                "limit", limit,
                "total", 0,
                "results", List.of()
            );
        }
        
        var limitedResults = tmdbResponse.getResults().stream()
            .limit(limit)
            .collect(Collectors.toList());
        
        var tmdbIds = limitedResults.stream()
            .map(TmdbSerieDto::getId)
            .filter(java.util.Objects::nonNull)
            .collect(Collectors.toList());
        
        var existingSeries = serieRepository.findByTmdbIdIn(tmdbIds);
        var existingMap = existingSeries.stream()
            .collect(Collectors.toMap(Serie::getTmdbId, serie -> serie));
        
        var results = limitedResults.stream()
            .map(tmdbSerie -> Map.of(
                "type", "serie",
                "tmdbId", tmdbSerie.getId(),
                "title", tmdbSerie.getName(),
                "poster_path", tmdbSerie.getPosterPath(),
                "overview", tmdbSerie.getOverview(),
                "first_air_date", tmdbSerie.getFirstAirDate(),
                "vote_average", tmdbSerie.getVoteAverage(),
                "local", existingMap.get(tmdbSerie.getId())
            ))
            .collect(Collectors.toList());
        
        return Map.of(
            "query", trimmedQuery,
            "limit", limit,
            "total", results.size(),
            "results", results
        );
    }

    // Méthodes utilitaires privées

    private void updateSerieFromDto(Serie serie, CreateSerieFromTmdbDto dto, TmdbSerieDto tmdbSerie) {
        if (StringUtils.hasText(dto.getTitleOverride())) {
            serie.setTitle(dto.getTitleOverride());
        }
        if (dto.getRating() != null) {
            serie.setRating(dto.getRating());
        }
        if (dto.getWishlist() != null) {
            serie.setWishlist(dto.getWishlist());
        }
        if (StringUtils.hasText(dto.getReview())) {
            serie.setReview(dto.getReview());
        }
        if (dto.getViewCount() != null) {
            serie.setViewCount(dto.getViewCount());
        }
        if (dto.getWatched() != null) {
            serie.setWatched(dto.getWatched());
        }
    }

    private void updateSerieFromUpdateDto(Serie serie, UpdateSerieDto dto) {
        if (StringUtils.hasText(dto.getTitle())) {
            serie.setTitle(dto.getTitle());
        }
        if (dto.getTmdbId() != null) {
            serie.setTmdbId(dto.getTmdbId());
        }
        if (dto.getRating() != null) {
            serie.setRating(dto.getRating());
        }
        if (dto.getWishlist() != null) {
            serie.setWishlist(dto.getWishlist());
        }
        if (StringUtils.hasText(dto.getReview())) {
            serie.setReview(dto.getReview());
        }
        if (dto.getViewCount() != null) {
            serie.setViewCount(dto.getViewCount());
        }
        if (dto.getWatched() != null) {
            serie.setWatched(dto.getWatched());
        }
    }

    private Map<String, Object> convertSerieToMap(Serie serie) {
        return Map.of(
            "id", serie.getId(),
            "title", serie.getTitle(),
            "tmdbId", serie.getTmdbId(),
            "rating", serie.getRating(),
            "wishlist", serie.getWishlist(),
            "review", serie.getReview() != null ? serie.getReview() : "",
            "viewCount", serie.getViewCount(),
            "watched", serie.getWatched(),
            "createdAt", serie.getCreatedAt(),
            "updatedAt", serie.getUpdatedAt()
        );
    }

    /**
     * Enrichit chaque série avec tmdb.poster_path (limitation: 5 requêtes parallèles max)
     */
    private List<Map<String, Object>> enrichPosterPath(List<Serie> series) {
        return series.parallelStream()
            .map(serie -> {
                Map<String, Object> serieMap = convertSerieToMap(serie);
                
                if (serie.getTmdbId() != null) {
                    try {
                        CompletableFuture<TmdbSerieDto> future = tmdbService.getSerieDetailsAsync(serie.getTmdbId());
                        TmdbSerieDto tmdbDetails = future.join(); // Attendre le résultat
                        Map<String, Object> tmdbMap = new HashMap<>();
                        tmdbMap.put("poster_path", tmdbDetails.getPosterPath());
                        serieMap.put("tmdb", tmdbMap);
                    } catch (Exception e) {
                        logger.debug("Impossible d'enrichir le poster pour la série {}: {}", 
                            serie.getId(), e.getMessage());
                        // On continue sans poster_path
                    }
                }
                
                return serieMap;
            })
            .collect(Collectors.toList());
    }
}
