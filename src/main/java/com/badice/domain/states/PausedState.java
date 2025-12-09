package com.badice.domain.states;

import com.badice.domain.services.GameEngine;

/**
 * Estado de pausa.
 */
public class PausedState implements GameState {
    private static final String STATE_NAME = "PAUSED";
    private final GameState previousState;

    public PausedState(GameState previousState) {
        this.previousState = previousState;
    }

    @Override
    public void onEnter(GameEngine engine) {
        engine.pauseGameTimer();
    }

    @Override
    public void update(GameEngine engine) {
        // No actualizar entidades mientras está pausado
    }

    @Override
    public void onExit(GameEngine engine) {
        engine.resumeGameTimer();
    }

    @Override
    public String getStateName() {
        return STATE_NAME;
    }

    public GameState getPreviousState() {
        return previousState;
    }
}
