package com.badice.domain.states;

import com.badice.domain.services.GameEngine;

/**
 * Estado del menú principal.
 */
public class MenuState implements GameState {
    private static final String STATE_NAME = "MENU";

    @Override
    public void onEnter(GameEngine engine) {
        System.out.println("Entrando al menú principal...");
    }

    @Override
    public void update(GameEngine engine) {
        // El menú es manejado por la capa de presentación
        // Este método se llama mientras el menú está activo
    }

    @Override
    public void onExit(GameEngine engine) {
        System.out.println("Saliendo del menú principal...");
    }

    @Override
    public String getStateName() {
        return STATE_NAME;
    }
}
