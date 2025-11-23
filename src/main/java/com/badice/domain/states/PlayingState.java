package com.badice.domain.states;

import com.badice.domain.services.GameEngine;

/**
 * Estado de juego activo (jugando).
 */
public class PlayingState implements GameState {
    private static final String STATE_NAME = "PLAYING";

    @Override
    public void onEnter(GameEngine engine) {
        System.out.println("Iniciando el juego...");
        engine.resetGameTimer();
    }

    @Override
    public void update(GameEngine engine) {
        // Actualizar todas las entidades
        engine.updateEntities();

        // Verificar condiciones de victoria/derrota
        if (engine.checkVictoryCondition()) {
            engine.changeState(new LevelCompleteState());
        } else if (engine.checkDefeatCondition()) {
            engine.changeState(new GameOverState());
        }
    }

    @Override
    public void onExit(GameEngine engine) {
        System.out.println("Saliendo del modo de juego...");
    }

    @Override
    public String getStateName() {
        return STATE_NAME;
    }
}
