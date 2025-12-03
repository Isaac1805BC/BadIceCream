package com.badice.presentation.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

/**
 * Panel para seleccionar el color del jugador.
 */
public class PlayerColorSelectionPanel extends JPanel {
    private JButton redButton;
    private JButton brownButton;
    private JButton blueButton;
    private JButton backButton;
    private JLabel titleLabel;
    private BufferedImage backgroundImage;
    private ResourceManager resourceManager;
    
    private String playerNumber = "1"; // "1" o "2"
    private String disabledColor = null; // Color ya seleccionado por otro jugador

    public PlayerColorSelectionPanel() {
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
        
        // Dibujar fondo con patrón repetido
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
        
        titleLabel = new JLabel("JUGADOR 1 - SELECCIONA TU COLOR");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
        titleLabel.setForeground(new Color(16, 25, 34));
        titlePanel.add(titleLabel);

        // Panel de botones de colores
        JPanel colorsPanel = new JPanel();
        colorsPanel.setLayout(new GridBagLayout());
        colorsPanel.setOpaque(false);
        colorsPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(15, 15, 15, 15);

        // Crear botones de color con sprites
        redButton = createColorButton("ROJO", "red");
        brownButton = createColorButton("CAFÉ", "brown");
        blueButton = createColorButton("VAINILLA", "player");

        colorsPanel.add(redButton, gbc);
        gbc.gridx++;
        colorsPanel.add(brownButton, gbc);
        gbc.gridx++;
        colorsPanel.add(blueButton, gbc);

        // Botón de volver
        JPanel bottomPanel = new JPanel();
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 30, 0));
        
        backButton = createIceButton("VOLVER");
        backButton.setPreferredSize(new Dimension(300, 50));
        bottomPanel.add(backButton);

        add(titlePanel, BorderLayout.NORTH);
        add(colorsPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private JButton createColorButton(String colorName, String colorKey) {
        JButton button = new JButton();
        button.setLayout(new BorderLayout());
        button.setPreferredSize(new Dimension(200, 200));
        button.setBackground(new Color(180, 220, 255));
        button.setFocusPainted(false);
        button.setBorderPainted(true);
        
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(100, 180, 255), 4),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        // Panel interno
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);

        // Cargar sprite del jugador
        BufferedImage sprite = resourceManager.loadImage("sprites/player/" + colorKey + "_down.png");
        if (sprite != null) {
            Image scaledSprite = sprite.getScaledInstance(80, 80, Image.SCALE_SMOOTH);
            JLabel spriteLabel = new JLabel(new ImageIcon(scaledSprite));
            spriteLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            contentPanel.add(spriteLabel);
            contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        }

        JLabel nameLabel = new JLabel(colorName);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 24));
        nameLabel.setForeground(new Color(20, 40, 80));
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(nameLabel);

        button.add(contentPanel, BorderLayout.CENTER);

        // Efecto hover
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (button.isEnabled()) {
                    button.setBackground(new Color(200, 240, 255));
                }
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (button.isEnabled()) {
                    button.setBackground(new Color(180, 220, 255));
                }
            }
        });

        return button;
    }

    private JButton createIceButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 18));
        button.setForeground(new Color(20, 40, 80));
        button.setBackground(new Color(180, 220, 255));
        button.setFocusPainted(false);
        button.setBorderPainted(true);
        
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
    public void setPlayerNumber(String number) {
        this.playerNumber = number;
        titleLabel.setText("JUGADOR " + number + " - SELECCIONA TU COLOR");
    }

    public void setDisabledColor(String color) {
        this.disabledColor = color;
        updateButtonStates();
    }

    private void updateButtonStates() {
        if (disabledColor != null) {
            switch (disabledColor) {
                case "red":
                    redButton.setEnabled(false);
                    redButton.setBackground(new Color(150, 150, 150));
                    break;
                case "brown":
                    brownButton.setEnabled(false);
                    brownButton.setBackground(new Color(150, 150, 150));
                    break;
                case "blue":
                    blueButton.setEnabled(false);
                    blueButton.setBackground(new Color(150, 150, 150));
                    break;
            }
        }
    }

    // Métodos para configurar listeners
    public void setRedButtonListener(ActionListener listener) {
        redButton.addActionListener(listener);
    }

    public void setBrownButtonListener(ActionListener listener) {
        brownButton.addActionListener(listener);
    }

    public void setBlueButtonListener(ActionListener listener) {
        blueButton.addActionListener(listener);
    }

    public void setBackButtonListener(ActionListener listener) {
        backButton.addActionListener(listener);
    }
}
