package com.badice.domain.states;

import com.badice.domain.services.GameEngine;

/**
 * Estado de nivel completado.
 */
public class LevelCompleteState implements GameState {
    private static final String STATE_NAME = "LEVEL_COMPLETE";

    @Override
    public void onEnter(GameEngine engine) {
        System.out.println("¡Nivel completado!");
        engine.pauseGameTimer();
    }

    @Override
    public void update(GameEngine engine) {
        // Esperar a que el usuario presione continuar
    }

    @Override
    public void onExit(GameEngine engine) {
        System.out.println("Cargando siguiente nivel...");
    }

    @Override
    public String getStateName() {
        return STATE_NAME;
    }
}
