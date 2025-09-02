package com.example.java_cine_api.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class JacksonConfig {

    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        
        // Support des types de date/temps Java 8+
        mapper.registerModule(new JavaTimeModule());
        
        // Ignorer les propriétés nulles lors de la sérialisation
        // mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        
        // Utiliser snake_case pour les propriétés JSON (compatible avec TMDB API)
        // Commenté pour garder camelCase comme NestJS
        // mapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        
        return mapper;
    }
}
