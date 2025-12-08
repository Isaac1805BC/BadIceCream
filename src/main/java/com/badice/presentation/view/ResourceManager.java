package com.badice.presentation.view;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Singleton que gestiona recursos gráficos (sprites/imágenes).
 */
public class ResourceManager {
    private static ResourceManager instance;
    private final Map<String, BufferedImage> imageCache;

    private ResourceManager() {
        this.imageCache = new HashMap<>();
    }

    public static ResourceManager getInstance() {
        if (instance == null) {
            instance = new ResourceManager();
        }
        return instance;
    }

    /**
     * Carga una imagen desde los recursos.
     */
    public BufferedImage loadImage(String path) {
        if (imageCache.containsKey(path)) {
            return imageCache.get(path);
        }

        try {
            InputStream is = getClass().getClassLoader().getResourceAsStream(path);
            if (is != null) {
                BufferedImage image = ImageIO.read(is);
                imageCache.put(path, image);
                return image;
            }
        } catch (IOException e) {
            // Silencioso - usar gráficos de fallback
        }

        imageCache.put(path, null);
        return null;
    }

    /**
     * Carga un sprite del jugador.
     */
    public BufferedImage getPlayerSprite(String color, String direction) {
        // Mapear "blue" al sprite "player" original
        String colorPrefix = color.equals("blue") ? "player" : color;
        return loadImage("sprites/player/" + colorPrefix + "_" + direction + ".png");
    }

    /**
     * Carga un sprite de enemigo.
     */
    public BufferedImage getEnemySprite(String enemyType, String direction) {
        if (enemyType.equalsIgnoreCase("squid")) {
            return loadImage("sprites/enemies/calamar.png");
        }
        return loadImage("sprites/enemies/" + enemyType + ".png");
    }

    /**
     * Carga un sprite de fruta.
     */
    public BufferedImage getFruitSprite(String fruitType) {
        // Mapear nombres comunes a los nombres de archivo exactos
        String filename = switch (fruitType.toLowerCase()) {
            case "banana", "platano" -> "Banana.png";
            case "uva", "grape" -> "Grape.png";
            case "cereza", "cherry" -> "Cherry.png";
            case "pina", "piña", "pineapple" -> "Pineapple.png";
            case "cactus" -> "Cactus.png";
            default -> fruitType + ".png"; // Fallback
        };

        return loadImage("sprites/fruits/" + filename);
    }

    /**
     * Carga un sprite de bloque.
     */
    public BufferedImage getBlockSprite(String blockType) {
        if (blockType.equals("wall")) {
            // Usar BorderMap para los bordes del nivel
            return loadImage("backgrounds/BorderMap.png");
        }
        return loadImage("sprites/blocks/" + blockType + ".png");
    }

    /**
     * Carga un background del juego.
     */
    public BufferedImage getBackground(String backgroundName) {
        return loadImage("backgrounds/" + backgroundName + ".jpg");
    }

    /**
     * Limpia la caché de imágenes.
     */
    public void clearCache() {
        imageCache.clear();
    }
}
