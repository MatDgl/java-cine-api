package com.example.java_cine_api.exception;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public ResourceNotFoundException(String resourceType, Long id) {
        super(String.format("%s avec l'ID %d introuvable", resourceType, id));
    }

    public ResourceNotFoundException(String resourceType, Integer tmdbId, String idType) {
        super(String.format("%s avec l'%s %d introuvable", resourceType, idType, tmdbId));
    }
}
