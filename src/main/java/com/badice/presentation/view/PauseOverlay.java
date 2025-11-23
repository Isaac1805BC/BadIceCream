package com.badice.presentation.view;

import javax.swing.*;
import java.awt.*;

/**
 * Overlay que aparece cuando el juego está pausado.
 */
public class PauseOverlay extends JPanel {
    private JButton resumeButton;
    private JButton mainMenuButton;

    public PauseOverlay() {
        setLayout(new GridBagLayout());
        setBackground(new Color(0, 0, 0, 180));
        setOpaque(false);

        initializeComponents();
    }

    private void initializeComponents() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.insets = new Insets(10, 10, 10, 10);

        // Título
        JLabel titleLabel = new JLabel("PAUSA");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 48));
        titleLabel.setForeground(Color.WHITE);
        gbc.gridy = 0;
        add(titleLabel, gbc);

        // Botón Reanudar
        resumeButton = createPauseButton("REANUDAR");
        gbc.gridy = 1;
        add(resumeButton, gbc);

        // Botón Menú Principal
        mainMenuButton = createPauseButton("MENÚ PRINCIPAL");
        gbc.gridy = 2;
        add(mainMenuButton, gbc);
    }

    private JButton createPauseButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 18));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(0, 100, 150));
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(250, 50));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(0, 150, 200));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(0, 100, 150));
            }
        });

        return button;
    }

    public JButton getResumeButton() {
        return resumeButton;
    }

    public JButton getMainMenuButton() {
        return mainMenuButton;
    }
}
