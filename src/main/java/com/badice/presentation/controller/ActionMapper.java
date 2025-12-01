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
        if (gameEngine.getStateManager().isInState(PlayingState.class)) {
            // Player 1 Controls (WASD)
            Direction d1 = getDirectionFromKeyP1(keyCode);
            if (d1 != null) {
                gameEngine.movePlayer(d1, 0); // 0 = Player 1
                return;
            }

            // Player 2 Controls (Arrows)
            Direction d2 = getDirectionFromKeyP2(keyCode);
            if (d2 != null) {
                gameEngine.movePlayer(d2, 1); // 1 = Player 2
                return;
            }

            // Actions
            if (keyCode == KeyEvent.VK_SPACE) {
                handleIceActionP1();
                return;
            }
            if (keyCode == KeyEvent.VK_ENTER) {
                handleIceActionP2();
                return;
            }
        }

        // Otras acciones
        switch (keyCode) {
            // case KeyEvent.VK_SPACE -> handleIceAction(); // Ya manejado arriba
            case KeyEvent.VK_P -> handlePauseToggle();
            case KeyEvent.VK_ESCAPE -> handleEscape();
            case KeyEvent.VK_R -> handleRestart();
        }
    }

    /**
     * Convierte una tecla a una dirección.
     */
    private Direction getDirectionFromKeyP1(int keyCode) {
        return switch (keyCode) {
            case KeyEvent.VK_W -> Direction.UP;
            case KeyEvent.VK_S -> Direction.DOWN;
            case KeyEvent.VK_A -> Direction.LEFT;
            case KeyEvent.VK_D -> Direction.RIGHT;
            default -> null;
        };
    }

    private Direction getDirectionFromKeyP2(int keyCode) {
        return switch (keyCode) {
            case KeyEvent.VK_UP -> Direction.UP;
            case KeyEvent.VK_DOWN -> Direction.DOWN;
            case KeyEvent.VK_LEFT -> Direction.LEFT;
            case KeyEvent.VK_RIGHT -> Direction.RIGHT;
            default -> null;
        };
    }

    /**
     * Maneja la acción de crear/destruir hielo.
     */
    private void handleIceActionP1() {
        handleIceAction(0);
    }

    private void handleIceActionP2() {
        handleIceAction(1);
    }

    private void handleIceAction(int playerIndex) {
        if (!gameEngine.getStateManager().isInState(PlayingState.class)) {
            return;
        }

        // Obtener lista de jugadores y verificar índice
        var players = gameEngine.getCurrentMap().getPlayers();
        if (playerIndex < 0 || playerIndex >= players.size())
            return;

        var player = players.get(playerIndex);
        if (player != null && player.isActive()) {
            Direction direction = player.getCurrentDirection();

            // Intentar destruir hielo primero
            boolean destroyed = gameEngine.playerDestroyIce(direction, playerIndex);

            // Si no se destruyó hielo, intentar crear
            if (!destroyed) {
                gameEngine.playerCreateIce(direction, playerIndex);
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
