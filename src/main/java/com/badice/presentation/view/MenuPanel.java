package com.badice.presentation.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

/**
 * Panel del menú principal.
 */
public class MenuPanel extends JPanel {
    private JButton playButton;
    private JButton selectLevelButton;
    private JButton exitButton;
    private BufferedImage backgroundImage;
    private BufferedImage logoImage;
    private ResourceManager resourceManager;

    public MenuPanel() {
        setLayout(new BorderLayout());

        // Inicializar ResourceManager
        resourceManager = ResourceManager.getInstance();

        // Cargar imágenes
        loadImages();

        initializeComponents();
    }

    private void loadImages() {
        backgroundImage = resourceManager.loadImage("backgrounds/menu_background.jpg");
        logoImage = resourceManager.loadImage("logo.png");
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Dibujar fondo con patrón repetido
        if (backgroundImage != null) {
            int imgWidth = backgroundImage.getWidth();
            int imgHeight = backgroundImage.getHeight();

            // Repetir la imagen en mosaico
            for (int x = 0; x < getWidth(); x += imgWidth) {
                for (int y = 0; y < getHeight(); y += imgHeight) {
                    g.drawImage(backgroundImage, x, y, null);
                }
            }
        } else {
            // Fondo de respaldo si no se carga la imagen
            g.setColor(new Color(20, 20, 40));
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    private void initializeComponents() {
        // Panel del título con logo
        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false); // Transparente para ver el fondo
        titlePanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0)); // Padding reducido

        // Cargar y mostrar logo
        if (logoImage != null) {
            int logoWidth = 400;
            int logoHeight = (int) (logoImage.getHeight() * ((double) logoWidth / logoImage.getWidth()));
            Image scaledLogo = logoImage.getScaledInstance(logoWidth, logoHeight, Image.SCALE_SMOOTH);
            JLabel logoLabel = new JLabel(new ImageIcon(scaledLogo));
            titlePanel.add(logoLabel);
        } else {
            // Fallback: usar texto si no se carga el logo
            JLabel titleLabel = new JLabel("BAD DOPO CREAM");
            titleLabel.setFont(new Font("Arial", Font.BOLD, 48));
            titleLabel.setForeground(new Color(0, 120, 215));
            titlePanel.add(titleLabel);
        }

        // Panel de botones - con padding reducido
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setOpaque(false); // Transparente para ver el fondo
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 200, 50, 200)); // Padding inferior reducido de 100 a
                                                                                  // 50

        // Botón JUGAR
        playButton = createMenuButton("JUGAR");

        // Botón SELECCIONAR NIVEL
        selectLevelButton = createMenuButton("SELECCIONAR NIVEL");

        // Botón SALIR
        exitButton = createMenuButton("SALIR");

        buttonPanel.add(Box.createVerticalGlue());
        buttonPanel.add(playButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        buttonPanel.add(selectLevelButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        buttonPanel.add(exitButton);
        buttonPanel.add(Box.createVerticalGlue());

        add(titlePanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);
    }

    private JButton createMenuButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 24));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(30, 144, 255)); // Azul brillante
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(400, 50));

        // Efecto hover
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(0, 120, 255));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(30, 144, 255));
            }
        });

        return button;
    }

    // Métodos para configurar listeners
    public void setPlayButtonListener(ActionListener listener) {
        playButton.addActionListener(listener);
    }

    public void setSelectLevelButtonListener(ActionListener listener) {
        selectLevelButton.addActionListener(listener);
    }

    public void setExitButtonListener(ActionListener listener) {
        exitButton.addActionListener(listener);
    }
}
