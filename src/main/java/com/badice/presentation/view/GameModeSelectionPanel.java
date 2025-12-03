package com.badice.presentation.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

/**
 * Panel para seleccionar el modo de juego con temática de hielo.
 */
public class GameModeSelectionPanel extends JPanel {
    private JButton onePlayerButton;
    private JButton pvpButton;
    private JButton pvmButton;
    private JButton mvmButton;
    private JButton backButton;
    private BufferedImage backgroundImage;
    private ResourceManager resourceManager;

    public GameModeSelectionPanel() {
        setLayout(new BorderLayout());
        initializeComponents();
        resourceManager = ResourceManager.getInstance();
        loadImages();
    }
    private void loadImages() {
        backgroundImage = resourceManager.loadImage("backgrounds/menu_background.jpg");
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
            g.setColor(new Color(30, 50, 90));
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    private void initializeComponents() {
        // Panel del título
        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(40, 0, 20, 0));
        
        JLabel titleLabel = new JLabel("SELECCIONA MODO DE JUEGO");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        titleLabel.setForeground(new Color(16, 25, 34));
        titlePanel.add(titleLabel);

        // Panel de botones
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridBagLayout());
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 100, 20, 100));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Crear botones con estilo de hielo
        onePlayerButton = createIceButton("1 JUGADOR");
        pvpButton = createIceButton("JUGADOR VS JUGADOR");
        pvmButton = createIceButton("JUGADOR VS MÁQUINA");
        mvmButton = createIceButton("MÁQUINA VS MÁQUINA");

        buttonPanel.add(onePlayerButton, gbc);
        gbc.gridy++;
        buttonPanel.add(pvpButton, gbc);
        gbc.gridy++;
        buttonPanel.add(pvmButton, gbc);
        gbc.gridy++;
        buttonPanel.add(mvmButton, gbc);

        // Botón de volver
        JPanel bottomPanel = new JPanel();
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 30, 0));
        
        backButton = createIceButton("VOLVER AL MENÚ");
        backButton.setPreferredSize(new Dimension(300, 50));
        bottomPanel.add(backButton);

        add(titlePanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private JButton createIceButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 20));
        button.setForeground(new Color(20, 40, 80));
        button.setBackground(new Color(180, 220, 255));
        button.setFocusPainted(false);
        button.setBorderPainted(true);
        button.setPreferredSize(new Dimension(400, 60));
        
        // Borde con efecto de hielo
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(100, 180, 255), 3),
            BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));

        // Efecto hover
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(200, 240, 255));
                button.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(150, 200, 255), 3),
                    BorderFactory.createEmptyBorder(10, 20, 10, 20)
                ));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(180, 220, 255));
                button.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(100, 180, 255), 3),
                    BorderFactory.createEmptyBorder(10, 20, 10, 20)
                ));
            }
        });

        return button;
    }

    // Métodos para configurar listeners
    public void setOnePlayerButtonListener(ActionListener listener) {
        onePlayerButton.addActionListener(listener);
    }

    public void setPvpButtonListener(ActionListener listener) {
        pvpButton.addActionListener(listener);
    }

    public void setPvmButtonListener(ActionListener listener) {
        pvmButton.addActionListener(listener);
    }

    public void setMvmButtonListener(ActionListener listener) {
        mvmButton.addActionListener(listener);
    }

    public void setBackButtonListener(ActionListener listener) {
        backButton.addActionListener(listener);
    }
}
