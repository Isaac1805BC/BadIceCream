package com.badice.presentation.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Panel que se muestra al finalizar el juego.
 */
public class GameOverPanel extends JPanel {
    private JLabel titleLabel;
    private JLabel scoreLabel;
    private JButton retryButton;
    private JButton mainMenuButton;

    public GameOverPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(20, 20, 40));

        initializeComponents();
    }

    private void initializeComponents() {
        // Panel superior con título y puntuación
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBackground(new Color(20, 20, 40));
        topPanel.setBorder(BorderFactory.createEmptyBorder(100, 50, 50, 50));

        titleLabel = new JLabel("GAME OVER");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 60));
        titleLabel.setForeground(Color.RED);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        scoreLabel = new JLabel("Puntuación: 0");
        scoreLabel.setFont(new Font("Arial", Font.PLAIN, 24));
        scoreLabel.setForeground(Color.WHITE);
        scoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        topPanel.add(titleLabel);
        topPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        topPanel.add(scoreLabel);

        // Panel de botones
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBackground(new Color(20, 20, 40));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(50, 200, 100, 200));

        retryButton = createButton("REINTENTAR");
        mainMenuButton = createButton("MENÚ PRINCIPAL");

        buttonPanel.add(retryButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        buttonPanel.add(mainMenuButton);

        add(topPanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 20));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(100, 0, 0));
        button.setFocusPainted(false);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(350, 60));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(150, 0, 0));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(100, 0, 0));
            }
        });

        return button;
    }

    public void setScore(int score) {
        scoreLabel.setText("Puntuación: " + score);
    }

    public void setRetryButtonListener(ActionListener listener) {
        retryButton.addActionListener(listener);
    }

    public void setMainMenuButtonListener(ActionListener listener) {
        mainMenuButton.addActionListener(listener);
    }
}
