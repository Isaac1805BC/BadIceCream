package com.badice.presentation.controller;

import com.badice.domain.entities.Direction;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashSet;
import java.util.Set;

/**
 * Captura la entrada del teclado del jugador.
 */
public class InputHandler implements KeyListener {
    private final Set<Integer> pressedKeys;
    private final ActionMapper actionMapper;

    public InputHandler(ActionMapper actionMapper) {
        this.pressedKeys = new HashSet<>();
        this.actionMapper = actionMapper;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        pressedKeys.add(keyCode);

        // Delegar al ActionMapper para procesar la acción
        actionMapper.handleKeyPress(keyCode);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        pressedKeys.remove(e.getKeyCode());
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // No necesitamos manejar keyTyped
    }

    /**
     * Verifica si una tecla está presionada actualmente.
     */
    public boolean isKeyPressed(int keyCode) {
        return pressedKeys.contains(keyCode);
    }

    /**
     * Obtiene la dirección actual basada en las teclas de flecha presionadas.
     */
    public Direction getCurrentDirection() {
        if (isKeyPressed(KeyEvent.VK_UP) || isKeyPressed(KeyEvent.VK_W)) {
            return Direction.UP;
        }
        if (isKeyPressed(KeyEvent.VK_DOWN) || isKeyPressed(KeyEvent.VK_S)) {
            return Direction.DOWN;
        }
        if (isKeyPressed(KeyEvent.VK_LEFT) || isKeyPressed(KeyEvent.VK_A)) {
            return Direction.LEFT;
        }
        if (isKeyPressed(KeyEvent.VK_RIGHT) || isKeyPressed(KeyEvent.VK_D)) {
            return Direction.RIGHT;
        }
        return null;
    }

    /**
     * Obtiene la dirección para el Jugador 1 (WASD).
     */
    public Direction getPlayer1Direction() {
        if (isKeyPressed(KeyEvent.VK_W))
            return Direction.UP;
        if (isKeyPressed(KeyEvent.VK_S))
            return Direction.DOWN;
        if (isKeyPressed(KeyEvent.VK_A))
            return Direction.LEFT;
        if (isKeyPressed(KeyEvent.VK_D))
            return Direction.RIGHT;
        return null;
    }

    /**
     * Obtiene la dirección para el Jugador 2 (Flechas).
     */
    public Direction getPlayer2Direction() {
        if (isKeyPressed(KeyEvent.VK_UP))
            return Direction.UP;
        if (isKeyPressed(KeyEvent.VK_DOWN))
            return Direction.DOWN;
        if (isKeyPressed(KeyEvent.VK_LEFT))
            return Direction.LEFT;
        if (isKeyPressed(KeyEvent.VK_RIGHT))
            return Direction.RIGHT;
        return null;
    }

    /**
     * Limpia todas las teclas presionadas.
     */
    public void clear() {
        pressedKeys.clear();
    }
}
