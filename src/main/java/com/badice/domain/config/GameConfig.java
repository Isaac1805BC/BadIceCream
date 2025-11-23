package com.badice.domain.config;

/**
 * Configuración global del juego.
 */
public class GameConfig {
    // Dimensiones del mapa
    public static final int DEFAULT_MAP_WIDTH = 15;
    public static final int DEFAULT_MAP_HEIGHT = 11;
    public static final int DEFAULT_CELL_SIZE = 32;

    // Velocidades
    public static final int PLAYER_SPEED = 1;
    public static final int ENEMY_SPEED = 1;

    // Puntuación
    public static final int POINTS_PER_FRUIT = 100;
    public static final int POINTS_PER_ENEMY = 500;
    public static final int POINTS_PER_LEVEL = 1000;

    // Juego
    public static final int INITIAL_LIVES = 3;
    public static final int MAX_LIVES = 5;

    // Timing (en milisegundos)
    public static final int GAME_TICK_RATE = 100; // 100ms = 10 FPS para lógica
    public static final int RENDER_FPS = 60;

    // Colores (para renderizado)
    public static final String COLOR_PLAYER = "#00FFFF"; // Cyan
    public static final String COLOR_ENEMY = "#FF0000"; // Red
    public static final String COLOR_FRUIT = "#FFD700"; // Gold
    public static final String COLOR_BLOCK = "#8B4513"; // Brown
    public static final String COLOR_ICE = "#87CEEB"; // Sky Blue

    private GameConfig() {
        // Clase de utilidad, no instanciable
    }
}
