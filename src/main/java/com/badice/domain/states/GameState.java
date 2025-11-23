package com.badice.domain.states;

import com.badice.domain.services.GameEngine;

/**
 * Interfaz base para los estados del juego (State Pattern).
 */
public interface GameState {
    /**
     * Llamado al entrar en este estado.
     */
    void onEnter(GameEngine engine);

    /**
     * Actualiza la lógica del estado.
     */
    void update(GameEngine engine);

    /**
     * Llamado al salir de este estado.
     */
    void onExit(GameEngine engine);

    /**
     * Devuelve el nombre del estado.
     */
    String getStateName();
}
