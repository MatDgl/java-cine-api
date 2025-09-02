-- Script d'initialisation de la base de données Cine API
-- Exécuté automatiquement au démarrage de PostgreSQL

-- Table Movie
CREATE TABLE IF NOT EXISTS Movie (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    tmdb_id INTEGER UNIQUE,
    rating NUMERIC(3,1) CHECK (rating >= 0 AND rating <= 10),
    wishlist BOOLEAN DEFAULT FALSE NOT NULL,
    review TEXT,
    view_count INTEGER DEFAULT 0 NOT NULL,
    watched BOOLEAN DEFAULT FALSE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);

-- Table Serie
CREATE TABLE IF NOT EXISTS Serie (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    tmdb_id INTEGER UNIQUE,
    rating NUMERIC(3,1) CHECK (rating >= 0 AND rating <= 10),
    wishlist BOOLEAN DEFAULT FALSE NOT NULL,
    review TEXT,
    view_count INTEGER DEFAULT 0 NOT NULL,
    watched BOOLEAN DEFAULT FALSE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);

-- Index pour améliorer les performances
CREATE INDEX IF NOT EXISTS idx_movie_tmdb_id ON movie(tmdb_id);
CREATE INDEX IF NOT EXISTS idx_movie_wishlist ON movie(wishlist);
CREATE INDEX IF NOT EXISTS idx_movie_rating ON movie(rating) WHERE rating IS NOT NULL;
CREATE INDEX IF NOT EXISTS idx_movie_watched ON movie(watched);

CREATE INDEX IF NOT EXISTS idx_serie_tmdb_id ON serie(tmdb_id);
CREATE INDEX IF NOT EXISTS idx_serie_wishlist ON serie(wishlist);
CREATE INDEX IF NOT EXISTS idx_serie_rating ON serie(rating) WHERE rating IS NOT NULL;
CREATE INDEX IF NOT EXISTS idx_serie_watched ON serie(watched);

-- Données d'exemple (optionnel)
INSERT INTO movie (title, tmdb_id, rating, wishlist, review, watched) 
VALUES 
    ('Fight Club', 550, 9.5, true, 'Un chef-d''œuvre absolu de David Fincher', true),
    ('The Matrix', 603, 9.0, false, 'Film révolutionnaire', true),
    ('Inception', 27205, 8.8, true, 'Complexe mais brillant', false)
ON CONFLICT (tmdb_id) DO NOTHING;

INSERT INTO serie (title, tmdb_id, rating, wishlist, review, watched)
VALUES
    ('Breaking Bad', 1396, 9.8, true, 'La meilleure série de tous les temps', true),
    ('Game of Thrones', 1399, 7.5, false, 'Excellente jusqu''à la saison 6', true),
    ('The Office', 2316, 8.5, true, 'Comédie parfaite', false)
ON CONFLICT (tmdb_id) DO NOTHING;

-- Fonction pour mettre à jour automatiquement updated_at
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Triggers pour mettre à jour updated_at automatiquement
CREATE TRIGGER update_movie_updated_at BEFORE UPDATE ON movie
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_serie_updated_at BEFORE UPDATE ON serie
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- Message de confirmation
SELECT 'Base de données Cine API initialisée avec succès!' AS message;
