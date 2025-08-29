package com.example.java_cine_api.controller;

import com.example.java_cine_api.dto.movie.CreateMovieDto;
import com.example.java_cine_api.entity.Movie;
import com.example.java_cine_api.service.MovieService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MovieController.class)
@SuppressWarnings("removal") // Supprimer l'avertissement de dépréciation temporairement
class MovieControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    @SuppressWarnings("removal")
    private MovieService movieService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCreateMovie() throws Exception {
        // Given
        CreateMovieDto createDto = new CreateMovieDto();
        createDto.setTitle("Test Movie");
        createDto.setRating(8.5f);
        createDto.setWishlist(true);

        Movie savedMovie = new Movie("Test Movie");
        savedMovie.setId(1L);

        when(movieService.create(any(CreateMovieDto.class))).thenReturn(savedMovie);

        // When & Then
        mockMvc.perform(post("/movie")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Test Movie"));
    }

    @Test
    void shouldGetAllMovies() throws Exception {
        // Given
        Map<String, Object> response = Map.of("items", List.of());
        when(movieService.findAll()).thenReturn(response);

        // When & Then
        mockMvc.perform(get("/movie"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items").isArray());
    }

    @Test
    void shouldSearchMovies() throws Exception {
        // Given
        Map<String, Object> searchResult = Map.of(
            "query", "test",
            "limit", 20,
            "total", 0,
            "results", List.of()
        );
        when(movieService.search("test", 20)).thenReturn(searchResult);

        // When & Then
        mockMvc.perform(get("/movie/search")
                .param("q", "test")
                .param("limit", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.query").value("test"))
                .andExpect(jsonPath("$.limit").value(20))
                .andExpect(jsonPath("$.results").isArray());
    }

    @Test
    void shouldValidateCreateMovieDto() throws Exception {
        // Given - DTO avec titre vide (validation échouera)
        CreateMovieDto invalidDto = new CreateMovieDto();
        invalidDto.setTitle(""); // Titre vide, devrait échouer
        invalidDto.setRating(15.0f); // Note > 10, devrait échouer

        // When & Then
        mockMvc.perform(post("/movie")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }
}
