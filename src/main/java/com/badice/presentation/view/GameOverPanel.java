package com.badice.presentation.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

/**
 * Panel que se muestra al finalizar el juego.
 */
public class GameOverPanel extends JPanel {

    private JLabel scoreLabel;
    private JButton retryButton;
    private JButton mainMenuButton;
    private BufferedImage backgroundImage;
    private ResourceManager resourceManager;

    public GameOverPanel() {
        setLayout(new BorderLayout());
        resourceManager = ResourceManager.getInstance();
        backgroundImage = resourceManager.loadImage("backgrounds/Fondo_game_over.jpg");

        initializeComponents();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Dibujar fondo
        if (backgroundImage != null) {
            // Escalar la imagen para que cubra todo el panel
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), null);
        } else {

            g.setColor(new Color(20, 20, 40));
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    private void initializeComponents() {
        // Panel superior con título y puntuación
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setOpaque(false);
        topPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        scoreLabel = new JLabel("Puntuación: 0");
        scoreLabel.setFont(new Font("Arial", Font.PLAIN, 24));
        scoreLabel.setForeground(Color.WHITE);
        scoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Panel de botones
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(250, 0, 50, 0));

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
        button.setBackground(new Color(0, 34, 255));
        button.setFocusPainted(false);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(350, 60));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(0, 34, 255));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(0, 34, 255));
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
