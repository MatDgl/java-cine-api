package com.example.java_cine_api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Arrays;

@Configuration
public class ApplicationConfig {

    @Value("${tmdb.bearer.token}")
    private String tmdbBearerToken;
    
    @Value("${cors.allowed.origins}")
    private String allowedOrigins;

    /**
     * Configuration du RestTemplate avec proxy automatique depuis les variables d'environnement
     */
    @Bean
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        
        // Configuration automatique du proxy depuis les variables d'environnement
        String httpProxy = System.getProperty("http.proxy", System.getenv("HTTP_PROXY"));
        String httpsProxy = System.getProperty("https.proxy", System.getenv("HTTPS_PROXY"));
        
        // Utiliser HTTPS_PROXY en priorité pour les requêtes HTTPS
        String proxyUrl = httpsProxy != null ? httpsProxy : httpProxy;
        
        if (proxyUrl != null && !proxyUrl.isEmpty()) {
            try {
                // Parser l'URL du proxy (format: http://host:port)
                if (proxyUrl.startsWith("http://")) {
                    String[] parts = proxyUrl.substring(7).split(":");
                    if (parts.length == 2) {
                        String proxyHost = parts[0];
                        int proxyPort = Integer.parseInt(parts[1]);
                        
                        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort));
                        factory.setProxy(proxy);
                        
                        System.out.println("🌐 Configuration proxy détectée: " + proxyHost + ":" + proxyPort);
                    }
                }
            } catch (Exception e) {
                System.err.println("❌ Erreur lors de la configuration du proxy: " + e.getMessage());
            }
        } else {
            System.out.println("ℹ️  Aucun proxy configuré - connexion directe");
        }
        
        // Configuration des timeouts
        factory.setConnectTimeout(30000); // 30 secondes
        factory.setReadTimeout(30000); // 30 secondes
        
        RestTemplate restTemplate = new RestTemplate(factory);
        
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
