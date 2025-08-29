package com.example.java_cine_api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
public class ApplicationConfig {

    @Value("${tmdb.bearer.token}")
    private String tmdbBearerToken;
    
    @Value("${cors.allowed.origins}")
    private String allowedOrigins;

    /**
     * Configuration du RestTemplate avec l'intercepteur pour les requêtes TMDB
     */
    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        
        // Ajouter l'intercepteur pour les headers TMDB
        ClientHttpRequestInterceptor tmdbInterceptor = (request, body, execution) -> {
            request.getHeaders().setBearerAuth(tmdbBearerToken);
            request.getHeaders().add("Accept", "application/json");
            return execution.execute(request, body);
        };
        
        restTemplate.getInterceptors().add(tmdbInterceptor);
        return restTemplate;
    }

    /**
     * Configuration CORS pour permettre les requêtes depuis le frontend
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Utiliser les origines définies dans application.properties
        configuration.setAllowedOrigins(Arrays.asList(allowedOrigins.split(",")));
        
        // Méthodes HTTP autorisées
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        
        // Headers autorisés
        configuration.setAllowedHeaders(Arrays.asList("*"));
        
        // Permettre les credentials
        configuration.setAllowCredentials(true);
        
        // Exposer certains headers
        configuration.setExposedHeaders(Arrays.asList("Authorization"));
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }
}
