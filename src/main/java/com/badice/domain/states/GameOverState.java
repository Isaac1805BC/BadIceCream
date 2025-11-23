package com.badice.domain.states;

import com.badice.domain.services.GameEngine;

/**
 * Estado de game over (derrota).
 */
public class GameOverState implements GameState {
    private static final String STATE_NAME = "GAME_OVER";

    @Override
    public void onEnter(GameEngine engine) {
        System.out.println("Game Over!");
        engine.pauseGameTimer();
    }

    @Override
    public void update(GameEngine engine) {
        // Esperar a que el usuario decida qué hacer
    }

    @Override
    public void onExit(GameEngine engine) {
        System.out.println("Saliendo del game over...");
    }

    @Override
    public String getStateName() {
        return STATE_NAME;
    }
}
