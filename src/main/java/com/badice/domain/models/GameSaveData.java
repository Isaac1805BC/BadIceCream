package com.badice.domain.models;

import com.badice.domain.entities.GameMap;
import com.badice.domain.entities.Player;

import java.io.Serializable;

/**
 * DTO que contiene todo el estado necesario para guardar y cargar una partida.
 */
public class GameSaveData implements Serializable {
    private static final long serialVersionUID = 1L;

    private GameMap gameMap;
    private Player player; // Referencia al jugador
    private int currentLevel;
    private int score;
    private int remainingLives;
    private long timeRemaining;

    public GameSaveData(GameMap gameMap, Player player, int currentLevel, int score, int remainingLives,
            long timeRemaining) {
        this.gameMap = gameMap;
        this.player = player;
        this.currentLevel = currentLevel;
        this.score = score;
        this.remainingLives = remainingLives;
        this.timeRemaining = timeRemaining;
    }

    public GameMap getGameMap() {
        return gameMap;
    }

    public Player getPlayer() {
        return player;
    }

    public int getCurrentLevel() {
        return currentLevel;
    }

    public int getScore() {
        return score;
    }

    public int getRemainingLives() {
        return remainingLives;
    }

    public long getTimeRemaining() {
        return timeRemaining;
    }
}
