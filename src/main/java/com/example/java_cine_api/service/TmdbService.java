package com.example.java_cine_api.service;

import com.example.java_cine_api.dto.tmdb.*;
import com.example.java_cine_api.exception.TmdbApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Service
public class TmdbService {

    private static final Logger logger = LoggerFactory.getLogger(TmdbService.class);

    private final RestTemplate restTemplate;
    private final String tmdbBaseUrl;
    private final Executor executor = Executors.newFixedThreadPool(10);

    public TmdbService(RestTemplate restTemplate, @Value("${tmdb.base.url}") String tmdbBaseUrl) {
        this.restTemplate = restTemplate;
        this.tmdbBaseUrl = tmdbBaseUrl;
    }

    /**
     * Recherche des films dans la base TMDB
     */
    public TmdbSearchResponseDto<TmdbMovieDto> searchMovies(String query) {
        logger.info("Recherche de films pour la requête: {}", query);
        
        String url = UriComponentsBuilder.fromUriString(tmdbBaseUrl)
                .path("/search/movie")
                .queryParam("query", query)
                .queryParam("language", "fr-FR")
                .toUriString();

        try {
            ParameterizedTypeReference<TmdbSearchResponseDto<TmdbMovieDto>> typeRef = 
                new ParameterizedTypeReference<TmdbSearchResponseDto<TmdbMovieDto>>() {};
            
            ResponseEntity<TmdbSearchResponseDto<TmdbMovieDto>> responseEntity = 
                restTemplate.exchange(url, HttpMethod.GET, null, typeRef);
            
            TmdbSearchResponseDto<TmdbMovieDto> response = responseEntity.getBody();
            logger.debug("Trouvé {} films pour la requête '{}'", 
                response != null ? response.getResults().size() : 0, query);
            return response;
        } catch (RestClientException e) {
            logger.error("Erreur lors de la recherche de films: {}", e.getMessage());
            throw new TmdbApiException("Erreur lors de la recherche de films TMDB", e);
        }
    }

    /**
     * Recherche des séries dans la base TMDB
     */
    public TmdbSearchResponseDto<TmdbSerieDto> searchSeries(String query) {
        logger.info("Recherche de séries pour la requête: {}", query);
        
        String url = UriComponentsBuilder.fromUriString(tmdbBaseUrl)
                .path("/search/tv")
                .queryParam("query", query)
                .queryParam("language", "fr-FR")
                .toUriString();

        try {
            ParameterizedTypeReference<TmdbSearchResponseDto<TmdbSerieDto>> typeRef = 
                new ParameterizedTypeReference<TmdbSearchResponseDto<TmdbSerieDto>>() {};
            
            ResponseEntity<TmdbSearchResponseDto<TmdbSerieDto>> responseEntity = 
                restTemplate.exchange(url, HttpMethod.GET, null, typeRef);
            
            TmdbSearchResponseDto<TmdbSerieDto> response = responseEntity.getBody();
            logger.debug("Trouvé {} séries pour la requête '{}'", 
                response != null ? response.getResults().size() : 0, query);
            return response;
        } catch (RestClientException e) {
            logger.error("Erreur lors de la recherche de séries: {}", e.getMessage());
            throw new TmdbApiException("Erreur lors de la recherche de séries TMDB", e);
        }
    }

    /**
     * Recherche multi (films + séries + personnes) et filtre pour ne garder que films/séries
     */
    public TmdbSearchResponseDto<TmdbMultiDto> searchMulti(String query) {
        logger.info("Recherche multi pour la requête: {}", query);
        
        String url = UriComponentsBuilder.fromUriString(tmdbBaseUrl)
                .path("/search/multi")
                .queryParam("query", query)
                .queryParam("language", "fr-FR")
                .toUriString();

        try {
            ParameterizedTypeReference<TmdbSearchResponseDto<TmdbMultiDto>> typeRef = 
                new ParameterizedTypeReference<TmdbSearchResponseDto<TmdbMultiDto>>() {};
            
            ResponseEntity<TmdbSearchResponseDto<TmdbMultiDto>> responseEntity = 
                restTemplate.exchange(url, HttpMethod.GET, null, typeRef);
            
            TmdbSearchResponseDto<TmdbMultiDto> response = responseEntity.getBody();
            
            if (response != null && response.getResults() != null) {
                // Filtrer pour ne garder que les films et séries
                List<TmdbMultiDto> filteredResults = response.getResults().stream()
                    .filter(item -> "movie".equals(item.getMediaType()) || "tv".equals(item.getMediaType()))
                    .toList();
                response.setResults(filteredResults);
            }
            
            logger.debug("Trouvé {} éléments multi pour la requête '{}'", 
                response != null ? response.getResults().size() : 0, query);
            return response;
        } catch (RestClientException e) {
            logger.error("Erreur lors de la recherche multi: {}", e.getMessage());
            throw new TmdbApiException("Erreur lors de la recherche multi TMDB", e);
        }
    }

    /**
     * Récupère les détails complets d'un film TMDB
     */
    public TmdbMovieDto getMovieDetails(Integer tmdbId) {
        logger.info("Récupération des détails du film TMDB ID: {}", tmdbId);
        
        String url = UriComponentsBuilder.fromUriString(tmdbBaseUrl)
                .path("/movie/{id}")
                .queryParam("append_to_response", "credits")
                .queryParam("language", "fr-FR")
                .buildAndExpand(tmdbId)
                .toUriString();

        try {
            TmdbMovieDto movie = restTemplate.getForObject(url, TmdbMovieDto.class);
            if (movie == null) {
                throw new TmdbApiException("Film TMDB non trouvé avec l'ID: " + tmdbId);
            }
            logger.debug("Détails récupérés pour le film: {}", movie.getTitle());
            return movie;
        } catch (RestClientException e) {
            logger.error("Erreur lors de la récupération du film {}: {}", tmdbId, e.getMessage());
            throw new TmdbApiException("Erreur lors de la récupération des détails du film TMDB", e);
        }
    }

    /**
     * Récupère les détails complets d'une série TMDB
     */
    public TmdbSerieDto getSerieDetails(Integer tmdbId) {
        logger.info("Récupération des détails de la série TMDB ID: {}", tmdbId);
        
        String url = UriComponentsBuilder.fromUriString(tmdbBaseUrl)
                .path("/tv/{id}")
                .queryParam("append_to_response", "credits")
                .queryParam("language", "fr-FR")
                .buildAndExpand(tmdbId)
                .toUriString();

        try {
            TmdbSerieDto serie = restTemplate.getForObject(url, TmdbSerieDto.class);
            if (serie == null) {
                throw new TmdbApiException("Série TMDB non trouvée avec l'ID: " + tmdbId);
            }
            logger.debug("Détails récupérés pour la série: {}", serie.getName());
            return serie;
        } catch (RestClientException e) {
            logger.error("Erreur lors de la récupération de la série {}: {}", tmdbId, e.getMessage());
            throw new TmdbApiException("Erreur lors de la récupération des détails de la série TMDB", e);
        }
    }

    /**
     * Récupère les détails d'un film de manière asynchrone (pour l'enrichissement poster_path)
     */
    public CompletableFuture<TmdbMovieDto> getMovieDetailsAsync(Integer tmdbId) {
        return CompletableFuture.supplyAsync(() -> getMovieDetails(tmdbId), executor);
    }

    /**
     * Récupère les détails d'une série de manière asynchrone (pour l'enrichissement poster_path)
     */
    public CompletableFuture<TmdbSerieDto> getSerieDetailsAsync(Integer tmdbId) {
        return CompletableFuture.supplyAsync(() -> getSerieDetails(tmdbId), executor);
    }
}
