package com.badice.presentation.view;

import javax.swing.*;
import java.awt.*;

/**
 * Ventana principal del juego.
 */
public class MainFrame extends JFrame {
    private static final String GAME_TITLE = "Bad Dopo Cream";
    private static final int WINDOW_WIDTH = 800;
    private static final int WINDOW_HEIGHT = 650;

    private JPanel currentPanel;

    public MainFrame() {
        initializeFrame();
    }

    private void initializeFrame() {
        setTitle(GAME_TITLE);
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Icono de la ventana (si existe)
        try {
            // Por ahora sin icono, se puede agregar después
        } catch (Exception e) {
            System.err.println("No se pudo cargar el icono de la ventana");
        }
    }

    /**
     * Muestra un panel específico en el frame.
     */
    public void showPanel(JPanel panel) {
        if (currentPanel != null) {
            remove(currentPanel);
        }

        currentPanel = panel;
        add(currentPanel, BorderLayout.CENTER);

        revalidate();
        repaint();

        // Si es un panel de juego, asegurarse de que tiene el foco
        currentPanel.requestFocusInWindow();
    }

    /**
     * Obtiene el panel actual.
     */
    public JPanel getCurrentPanel() {
        return currentPanel;
    }
}
