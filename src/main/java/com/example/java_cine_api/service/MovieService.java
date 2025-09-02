package com.example.java_cine_api.service;

import com.example.java_cine_api.dto.movie.*;
import com.example.java_cine_api.dto.tmdb.TmdbMovieDto;
import com.example.java_cine_api.entity.Movie;
import com.example.java_cine_api.exception.ResourceNotFoundException;
import com.example.java_cine_api.repository.MovieRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@Transactional
public class MovieService {

    private static final Logger logger = LoggerFactory.getLogger(MovieService.class);

    private final MovieRepository movieRepository;
    private final TmdbService tmdbService;

    public MovieService(MovieRepository movieRepository, TmdbService tmdbService) {
        this.movieRepository = movieRepository;
        this.tmdbService = tmdbService;
    }

    /**
     * Crée un nouveau film
     */
    public Movie create(CreateMovieDto dto) {
        logger.info("Création d'un nouveau film: {}", dto.getTitle());
        
        Movie movie = new Movie(
            dto.getTitle(),
            dto.getTmdbId(),
            dto.getRating(),
            dto.getWishlist(),
            dto.getReview(),
            dto.getViewCount(),
            dto.getWatched()
        );
        
        Movie savedMovie = movieRepository.save(movie);
        logger.debug("Film créé avec l'ID: {}", savedMovie.getId());
        return savedMovie;
    }

    /**
     * Crée (ou met à jour) un film local à partir d'un tmdbId. Si un film avec ce tmdbId
     * existe déjà on met simplement à jour les champs utilisateurs fournis.
     */
    public Movie createFromTmdb(CreateMovieFromTmdbDto dto) {
        logger.info("Création/mise à jour d'un film depuis TMDB ID: {}", dto.getTmdbId());
        
        // Récupérer les détails depuis TMDB
        TmdbMovieDto tmdbMovie = tmdbService.getMovieDetails(dto.getTmdbId());
        
        // Vérifier si le film existe déjà localement
        Movie existingMovie = movieRepository.findByTmdbId(dto.getTmdbId()).orElse(null);
        
        if (existingMovie != null) {
            // Mettre à jour le film existant avec les nouvelles données utilisateur
            logger.debug("Mise à jour du film existant: {}", existingMovie.getTitle());
            updateMovieFromDto(existingMovie, dto, tmdbMovie);
            return movieRepository.save(existingMovie);
        } else {
            // Créer un nouveau film
            String title = StringUtils.hasText(dto.getTitleOverride()) 
                ? dto.getTitleOverride() 
                : tmdbMovie.getTitle();
                
            Movie newMovie = new Movie(
                title,
                dto.getTmdbId(),
                dto.getRating(),
                dto.getWishlist(),
                dto.getReview(),
                dto.getViewCount(),
                dto.getWatched()
            );
            
            Movie savedMovie = movieRepository.save(newMovie);
            logger.debug("Nouveau film créé depuis TMDB: {}", savedMovie.getTitle());
            return savedMovie;
        }
    }

    /**
     * Récupère tous les films avec enrichissement du poster_path
     */
    public Map<String, Object> findAll() {
        logger.info("Récupération de tous les films");
        
        List<Movie> movies = movieRepository.findAllByOrderByCreatedAtDesc();
        List<Map<String, Object>> enrichedMovies = enrichPosterPath(movies);
        
        Map<String, Object> result = new HashMap<>();
        result.put("items", enrichedMovies);
        return result;
    }

    /**
     * Récupère tous les films en wishlist avec enrichissement du poster_path
     */
    public Map<String, Object> findWishlist() {
        logger.info("Récupération des films en wishlist");
        
        List<Movie> movies = movieRepository.findByWishlistTrue();
        List<Map<String, Object>> enrichedMovies = enrichPosterPath(movies);
        
        Map<String, Object> result = new HashMap<>();
        result.put("items", enrichedMovies);
        return result;
    }

    /**
     * Récupère tous les films notés avec enrichissement du poster_path
     */
    public Map<String, Object> findRated() {
        logger.info("Récupération des films notés");
        List<Movie> movies = movieRepository.findByRatingIsNotNull();
        List<Map<String, Object>> enriched = enrichPosterPath(movies);
        
        Map<String, Object> result = new HashMap<>();
        result.put("items", enriched);
        result.put("total", enriched.size());
        return result;
    }

    /**
     * Récupère un film par son ID local
     */
    @Transactional(readOnly = true)
    public Map<String, Object> findOne(Long id) {
        logger.info("Récupération du film avec ID: {}", id);
        
        Movie movie = movieRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Film", id));
        
        Map<String, Object> result = convertMovieToMap(movie);
        
        // Enrichir avec les données TMDB si disponibles
        if (movie.getTmdbId() != null) {
            try {
                TmdbMovieDto tmdbMovie = tmdbService.getMovieDetails(movie.getTmdbId());
                result.put("tmdb", tmdbMovie);
            } catch (Exception e) {
                logger.warn("Impossible de récupérer les détails TMDB pour le film {}: {}", 
                    movie.getId(), e.getMessage());
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
        logger.info("Récupération du film via TMDB ID: {}", tmdbId);
        
        TmdbMovieDto tmdbMovie = tmdbService.getMovieDetails(tmdbId);
        Movie localMovie = movieRepository.findByTmdbId(tmdbId).orElse(null);
        
        Map<String, Object> result = new HashMap<>();
        result.put("tmdb", tmdbMovie);
        result.put("local", localMovie);
        return result;
    }

    /**
     * Met à jour un film
     */
    public Movie update(Long id, UpdateMovieDto dto) {
        logger.info("Mise à jour du film avec ID: {}", id);
        
        Movie movie = movieRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Film", id));
        
        updateMovieFromUpdateDto(movie, dto);
        
        Movie updatedMovie = movieRepository.save(movie);
        logger.debug("Film mis à jour: {}", updatedMovie.getTitle());
        return updatedMovie;
    }

    /**
     * Supprime un film
     */
    public Movie remove(Long id) {
        logger.info("Suppression du film avec ID: {}", id);
        
        Movie movie = movieRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Film", id));
        
        movieRepository.delete(movie);
        logger.debug("Film supprimé: {}", movie.getTitle());
        return movie;
    }

    /**
     * Recherche des films via TMDB et indique si déjà présent localement
     */
    @Transactional(readOnly = true)
    public Map<String, Object> search(String query, int limit) {
        String trimmedQuery = query != null ? query.trim() : "";
        
        if (trimmedQuery.isEmpty()) {
            Map<String, Object> emptyResult = new HashMap<>();
            emptyResult.put("query", trimmedQuery);
            emptyResult.put("limit", limit);
            emptyResult.put("total", 0);
            emptyResult.put("results", List.of());
            return emptyResult;
        }
        
        logger.info("Recherche de films pour la requête: {} (limite: {})", trimmedQuery, limit);
        
        var tmdbResponse = tmdbService.searchMovies(trimmedQuery);
        
        if (tmdbResponse == null || tmdbResponse.getResults() == null) {
            Map<String, Object> emptyResult = new HashMap<>();
            emptyResult.put("query", trimmedQuery);
            emptyResult.put("limit", limit);
            emptyResult.put("total", 0);
            emptyResult.put("results", List.of());
            return emptyResult;
        }
        
        var limitedResults = tmdbResponse.getResults().stream()
            .limit(limit)
            .collect(Collectors.toList());
        
        var tmdbIds = limitedResults.stream()
            .map(TmdbMovieDto::getId)
            .filter(java.util.Objects::nonNull)
            .collect(Collectors.toList());
        
        var existingMovies = movieRepository.findByTmdbIdIn(tmdbIds);
        var existingMap = existingMovies.stream()
            .collect(Collectors.toMap(Movie::getTmdbId, movie -> movie));
        
        var results = limitedResults.stream()
            .map(tmdbMovie -> {
                Map<String, Object> movieMap = new HashMap<>();
                movieMap.put("type", "movie");
                movieMap.put("tmdbId", tmdbMovie.getId());
                movieMap.put("title", tmdbMovie.getTitle());
                movieMap.put("poster_path", tmdbMovie.getPosterPath());
                movieMap.put("overview", tmdbMovie.getOverview());
                movieMap.put("release_date", tmdbMovie.getReleaseDate());
                movieMap.put("vote_average", tmdbMovie.getVoteAverage());
                movieMap.put("local", existingMap.get(tmdbMovie.getId()));
                return movieMap;
            })
            .collect(Collectors.toList());
        
        Map<String, Object> searchResult = new HashMap<>();
        searchResult.put("query", trimmedQuery);
        searchResult.put("limit", limit);
        searchResult.put("total", results.size());
        searchResult.put("results", results);
        return searchResult;
    }

    // Méthodes utilitaires privées

    private void updateMovieFromDto(Movie movie, CreateMovieFromTmdbDto dto, TmdbMovieDto tmdbMovie) {
        if (StringUtils.hasText(dto.getTitleOverride())) {
            movie.setTitle(dto.getTitleOverride());
        }
        if (dto.getRating() != null) {
            movie.setRating(dto.getRating());
        }
        if (dto.getWishlist() != null) {
            movie.setWishlist(dto.getWishlist());
        }
        if (StringUtils.hasText(dto.getReview())) {
            movie.setReview(dto.getReview());
        }
        if (dto.getViewCount() != null) {
            movie.setViewCount(dto.getViewCount());
        }
        if (dto.getWatched() != null) {
            movie.setWatched(dto.getWatched());
        }
    }

    private void updateMovieFromUpdateDto(Movie movie, UpdateMovieDto dto) {
        if (StringUtils.hasText(dto.getTitle())) {
            movie.setTitle(dto.getTitle());
        }
        if (dto.getTmdbId() != null) {
            movie.setTmdbId(dto.getTmdbId());
        }
        if (dto.getRating() != null) {
            movie.setRating(dto.getRating());
        }
        if (dto.getWishlist() != null) {
            movie.setWishlist(dto.getWishlist());
        }
        if (StringUtils.hasText(dto.getReview())) {
            movie.setReview(dto.getReview());
        }
        if (dto.getViewCount() != null) {
            movie.setViewCount(dto.getViewCount());
        }
        if (dto.getWatched() != null) {
            movie.setWatched(dto.getWatched());
        }
    }

    private Map<String, Object> convertMovieToMap(Movie movie) {
        Map<String, Object> movieMap = new HashMap<>();
        movieMap.put("id", movie.getId());
        movieMap.put("title", movie.getTitle());
        movieMap.put("tmdbId", movie.getTmdbId());
        movieMap.put("rating", movie.getRating());
        movieMap.put("wishlist", movie.getWishlist());
        movieMap.put("review", movie.getReview() != null ? movie.getReview() : "");
        movieMap.put("viewCount", movie.getViewCount());
        movieMap.put("watched", movie.getWatched());
        movieMap.put("createdAt", movie.getCreatedAt());
        movieMap.put("updatedAt", movie.getUpdatedAt());
        return movieMap;
    }

    /**
     * Enrichit chaque film avec tmdb.poster_path (limitation: 5 requêtes parallèles max)
     */
    private List<Map<String, Object>> enrichPosterPath(List<Movie> movies) {
        return movies.parallelStream()
            .map(movie -> {
                Map<String, Object> movieMap = convertMovieToMap(movie);
                
                if (movie.getTmdbId() != null) {
                    try {
                        CompletableFuture<TmdbMovieDto> future = tmdbService.getMovieDetailsAsync(movie.getTmdbId());
                        TmdbMovieDto tmdbDetails = future.join(); // Attendre le résultat
                        
                        Map<String, Object> tmdbMap = new HashMap<>();
                        tmdbMap.put("poster_path", tmdbDetails.getPosterPath());
                        movieMap.put("tmdb", tmdbMap);
                    } catch (Exception e) {
                        logger.debug("Impossible d'enrichir le poster pour le film {}: {}", 
                            movie.getId(), e.getMessage());
                        // On continue sans poster_path
                    }
                }
                
                return movieMap;
            })
            .collect(Collectors.toList());
    }
}
