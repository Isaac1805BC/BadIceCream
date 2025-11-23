package com.badice;

import com.badice.presentation.controller.GameController;

import javax.swing.SwingUtilities;

/**
 * Punto de entrada principal del juego Bad Dopo Cream.
 */
public class Main {
    public static void main(String[] args) {
        // Ejecutar en el Event Dispatch Thread de Swing
        SwingUtilities.invokeLater(() -> {
            GameController controller = new GameController();
            controller.start();
        });
    }
}
