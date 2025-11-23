package com.badice.presentation.controller;

import com.badice.domain.entities.Direction;
import com.badice.domain.services.GameEngine;
import com.badice.domain.states.PausedState;
import com.badice.domain.states.PlayingState;

import java.awt.event.KeyEvent;

/**
 * Traduce las teclas presionadas a acciones del dominio.
 */
public class ActionMapper {
    private final GameEngine gameEngine;

    public ActionMapper(GameEngine gameEngine) {
        this.gameEngine = gameEngine;
    }

    /**
     * Procesa una tecla presionada y ejecuta la acción correspondiente.
     */
    public void handleKeyPress(int keyCode) {
        // Movimiento del jugador (flechas o WASD)
        Direction direction = getDirectionFromKey(keyCode);
        if (direction != null && gameEngine.getStateManager().isInState(PlayingState.class)) {
            gameEngine.movePlayer(direction);
            return;
        }

        // Otras acciones
        switch (keyCode) {
            case KeyEvent.VK_SPACE -> handleIceAction();
            case KeyEvent.VK_P -> handlePauseToggle();
            case KeyEvent.VK_ESCAPE -> handleEscape();
            case KeyEvent.VK_R -> handleRestart();
        }
    }

    /**
     * Convierte una tecla a una dirección.
     */
    private Direction getDirectionFromKey(int keyCode) {
        return switch (keyCode) {
            case KeyEvent.VK_UP, KeyEvent.VK_W -> Direction.UP;
            case KeyEvent.VK_DOWN, KeyEvent.VK_S -> Direction.DOWN;
            case KeyEvent.VK_LEFT, KeyEvent.VK_A -> Direction.LEFT;
            case KeyEvent.VK_RIGHT, KeyEvent.VK_D -> Direction.RIGHT;
            default -> null;
        };
    }

    /**
     * Maneja la acción de crear/destruir hielo.
     */
    private void handleIceAction() {
        if (!gameEngine.getStateManager().isInState(PlayingState.class)) {
            return;
        }

        var player = gameEngine.getPlayer();
        if (player != null) {
            Direction direction = player.getCurrentDirection();

            // Intentar destruir hielo primero
            boolean destroyed = gameEngine.playerDestroyIce(direction);

            // Si no se destruyó hielo, intentar crear
            if (!destroyed) {
                gameEngine.playerCreateIce(direction);
            }
        }
    }

    /**
     * Alterna entre pausa y juego.
     */
    private void handlePauseToggle() {
        var stateManager = gameEngine.getStateManager();

        if (stateManager.isInState(PlayingState.class)) {
            stateManager.changeState(new PausedState(stateManager.getCurrentState()));
        } else if (stateManager.isInState(PausedState.class)) {
            PausedState pausedState = (PausedState) stateManager.getCurrentState();
            stateManager.changeState(pausedState.getPreviousState());
        }
    }

    /**
     * Maneja la tecla ESC.
     */
    private void handleEscape() {
        // Similar a pausa
        handlePauseToggle();
    }

    /**
     * Reinicia el nivel actual.
     */
    private void handleRestart() {
        if (gameEngine.getStateManager().isInState(PlayingState.class)) {
            gameEngine.restartLevel();
        }
    }
}
