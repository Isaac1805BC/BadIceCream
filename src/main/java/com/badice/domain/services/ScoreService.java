package com.badice.domain.services;

import com.badice.domain.config.GameConfig;

/**
 * Servicio que maneja la puntuación y el progreso del jugador.
 */
public class ScoreService {
    private int currentScore;
    private int highScore;
    private int currentLevel;
    private int fruitsCollected;
    private int totalFruits;

    public ScoreService() {
        this.currentScore = 0;
        this.highScore = 0;
        this.currentLevel = 1;
        this.fruitsCollected = 0;
        this.totalFruits = 0;
    }

    /**
     * Añade puntos por recoger una fruta.
     */
    public void addFruitScore(int points) {
        currentScore += points;
        fruitsCollected++;
        updateHighScore();
    }

    /**
     * Añade puntos por derrotar un enemigo.
     */
    public void addEnemyScore() {
        currentScore += GameConfig.POINTS_PER_ENEMY;
        updateHighScore();
    }

    /**
     * Añade puntos por completar un nivel.
     */
    public void addLevelCompletionScore() {
        currentScore += GameConfig.POINTS_PER_LEVEL;
        updateHighScore();
    }

    /**
     * Añade puntos personalizados.
     */
    public void addScore(int points) {
        currentScore += points;
        updateHighScore();
    }

    private void updateHighScore() {
        if (currentScore > highScore) {
            highScore = currentScore;
        }
    }

    /**
     * Reinicia la puntuación actual (para un nuevo juego).
     */
    public void resetCurrentScore() {
        this.currentScore = 0;
        this.currentLevel = 1;
        this.fruitsCollected = 0;
        this.totalFruits = 0;
    }

    /**
     * Avanza al siguiente nivel.
     */
    public void nextLevel() {
        currentLevel++;
        fruitsCollected = 0;
    }

    /**
     * Verifica si se han recolectado todas las frutas del nivel.
     */
    public boolean areAllFruitsCollected() {
        return totalFruits > 0 && fruitsCollected >= totalFruits;
    }

    // Getters
    public int getCurrentScore() {
        return currentScore;
    }

    public int getHighScore() {
        return highScore;
    }

    public int getCurrentLevel() {
        return currentLevel;
    }

    public int getFruitsCollected() {
        return fruitsCollected;
    }

    public int getTotalFruits() {
        return totalFruits;
    }

    public void setTotalFruits(int totalFruits) {
        this.totalFruits = totalFruits;
    }

    public void setCurrentLevel(int level) {
        this.currentLevel = level;
    }
}
