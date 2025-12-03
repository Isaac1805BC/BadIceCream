package com.badice.presentation.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

/**
 * Panel que muestra al ganador después de completar un nivel en modo multijugador.
 */
public class VictoryPanel extends JPanel {
    private JButton nextLevelButton;
    private JButton mainMenuButton;
    private JLabel winnerLabel;
    private JLabel pointsLabel;
    private JLabel winnerSpriteLabel;
    private BufferedImage backgroundImage;
    private ResourceManager resourceManager;
    
    private String winnerColor = "blue";
    private int winnerPoints = 0;

    public VictoryPanel() {
        setLayout(new BorderLayout());
        resourceManager = ResourceManager.getInstance();
        loadImages();
        initializeComponents();
    }

    private void loadImages() {
        backgroundImage = resourceManager.loadImage("backgrounds/menu_background.jpg");
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        // Dibujar fondo
        if (backgroundImage != null) {
            int imgWidth = backgroundImage.getWidth();
            int imgHeight = backgroundImage.getHeight();

            for (int x = 0; x < getWidth(); x += imgWidth) {
                for (int y = 0; y < getHeight(); y += imgHeight) {
                    g.drawImage(backgroundImage, x, y, null);
                }
            }
        } else {
            g.setColor(new Color(30, 50, 90));
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    private void initializeComponents() {
        // Panel del título
        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(40, 0, 20, 0));
        
        winnerLabel = new JLabel("¡GANADOR!");
        winnerLabel.setFont(new Font("Arial", Font.BOLD, 48));
        winnerLabel.setForeground(new Color(16, 25, 34));
        titlePanel.add(winnerLabel);

        // Panel central con sprite y puntos
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        // Sprite del ganador
        winnerSpriteLabel = new JLabel();
        winnerSpriteLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(winnerSpriteLabel);
        
        centerPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Puntos
        pointsLabel = new JLabel("0 PUNTOS");
        pointsLabel.setFont(new Font("Arial", Font.BOLD, 36));
        pointsLabel.setForeground(new Color(16, 25, 34));
        pointsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(pointsLabel);

        // Panel de botones
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 30, 0));
        
        nextLevelButton = createIceButton("SIGUIENTE NIVEL");
        mainMenuButton = createIceButton("MENÚ PRINCIPAL");
        
        buttonPanel.add(nextLevelButton);
        buttonPanel.add(mainMenuButton);

        add(titlePanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JButton createIceButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 18));
        button.setForeground(new Color(20, 40, 80));
        button.setBackground(new Color(180, 220, 255));
        button.setFocusPainted(false);
        button.setBorderPainted(true);
        button.setPreferredSize(new Dimension(250, 50));
        
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(100, 180, 255), 3),
            BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(200, 240, 255));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(180, 220, 255));
            }
        });

        return button;
    }

    // Métodos para configurar el panel
    public void setWinner(String color, int points) {
        this.winnerColor = color;
        this.winnerPoints = points;
        updateDisplay();
    }

    private void updateDisplay() {
        pointsLabel.setText(winnerPoints + " PUNTOS");
        
        // Cargar sprite del ganador
        String colorPrefix = winnerColor.equals("blue") ? "player" : winnerColor;
        BufferedImage sprite = resourceManager.loadImage("sprites/player/" + colorPrefix + "_down.png");
        if (sprite != null) {
            Image scaledSprite = sprite.getScaledInstance(150, 150, Image.SCALE_SMOOTH);
            winnerSpriteLabel.setIcon(new ImageIcon(scaledSprite));
        }
    }

    // Métodos para configurar listeners
    public void setNextLevelButtonListener(ActionListener listener) {
        nextLevelButton.addActionListener(listener);
    }

    public void setMainMenuButtonListener(ActionListener listener) {
        mainMenuButton.addActionListener(listener);
    }
}
