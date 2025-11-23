package com.badice.domain.services;

import com.badice.domain.states.GameState;

/**
 * Servicio que maneja las transiciones entre estados del juego.
 */
public class GameStateManager {
    private GameState currentState;
    private GameEngine engine;

    public GameStateManager(GameEngine engine) {
        this.engine = engine;
    }

    /**
     * Cambia al estado especificado.
     */
    public void changeState(GameState newState) {
        if (currentState != null) {
            currentState.onExit(engine);
        }

        currentState = newState;

        if (currentState != null) {
            currentState.onEnter(engine);
        }
    }

    /**
     * Actualiza el estado actual.
     */
    public void update() {
        if (currentState != null) {
            currentState.update(engine);
        }
    }

    /**
     * Obtiene el estado actual.
     */
    public GameState getCurrentState() {
        return currentState;
    }

    /**
     * Verifica si está en un estado específico.
     */
    public boolean isInState(Class<? extends GameState> stateClass) {
        return currentState != null && stateClass.isInstance(currentState);
    }

    /**
     * Obtiene el nombre del estado actual.
     */
    public String getCurrentStateName() {
        return currentState != null ? currentState.getStateName() : "NONE";
    }
}
