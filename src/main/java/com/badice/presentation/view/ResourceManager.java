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
    public BufferedImage getPlayerSprite(String direction) {
        return loadImage("sprites/player/player_" + direction + ".png");
    }

    /**
     * Carga un sprite de enemigo.
     */
    public BufferedImage getEnemySprite(String enemyType, String direction) {
        return loadImage("sprites/enemies/" + enemyType + "_" + direction + ".png");
    }

    /**
     * Carga un sprite de fruta.
     */
    public BufferedImage getFruitSprite(String fruitType) {
        return loadImage("sprites/fruits/" + fruitType + ".png");
    }

    /**
     * Carga un sprite de bloque.
     */
    public BufferedImage getBlockSprite(String blockType) {
        return loadImage("sprites/blocks/" + blockType + ".png");
    }

    /**
     * Limpia la caché de imágenes.
     */
    public void clearCache() {
        imageCache.clear();
    }
}
