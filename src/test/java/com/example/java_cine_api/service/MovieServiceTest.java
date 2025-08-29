package com.example.java_cine_api.service;

import com.example.java_cine_api.dto.movie.CreateMovieDto;
import com.example.java_cine_api.entity.Movie;
import com.example.java_cine_api.repository.MovieRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MovieServiceTest {

    @Mock
    private MovieRepository movieRepository;

    @Mock
    private TmdbService tmdbService;

    @InjectMocks
    private MovieService movieService;

    @Test
    void shouldCreateMovie() {
        // Given
        CreateMovieDto createDto = new CreateMovieDto();
        createDto.setTitle("Test Movie");
        createDto.setRating(8.5f);
        createDto.setWishlist(true);

        Movie savedMovie = new Movie("Test Movie");
        savedMovie.setId(1L);

        when(movieRepository.save(any(Movie.class))).thenReturn(savedMovie);

        // When
        Movie result = movieService.create(createDto);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Movie", result.getTitle());
        verify(movieRepository, times(1)).save(any(Movie.class));
    }

    @Test
    void shouldFindMovieById() {
        // Given
        Long movieId = 1L;
        Movie movie = new Movie("Test Movie");
        movie.setId(movieId);

        when(movieRepository.findById(movieId)).thenReturn(Optional.of(movie));

        // When
        var result = movieService.findOne(movieId);

        // Then
        assertNotNull(result);
        assertTrue(result.containsKey("id"));
        assertEquals(movieId, result.get("id"));
        assertEquals("Test Movie", result.get("title"));
        verify(movieRepository, times(1)).findById(movieId);
    }
}
