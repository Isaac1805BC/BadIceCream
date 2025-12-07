package com.badice.presentation.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

/**
 * Panel para seleccionar el nivel con temática de hielo.
 */
public class LevelSelectionPanel extends JPanel {
    private JButton level1Button;
    private JButton level2Button;
    private JButton level3Button;
    private JButton level4Button;
    private JButton backButton;
    private BufferedImage backgroundImage;
    private ResourceManager resourceManager;

    public LevelSelectionPanel() {
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

        JLabel titleLabel = new JLabel("SELECCIONA NIVEL");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        titleLabel.setForeground(new Color(16, 25, 34));
        titlePanel.add(titleLabel);

        // Panel de botones de niveles
        JPanel levelsPanel = new JPanel();
        levelsPanel.setLayout(new GridBagLayout());
        levelsPanel.setOpaque(false);
        levelsPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(15, 15, 15, 15);

        // Crear botones de nivel con estilo de hielo
        level1Button = createLevelButton("NIVEL 1", "Plátanos y Uvas");
        level2Button = createLevelButton("NIVEL 2", "Cerezas y Piñas");
        level3Button = createLevelButton("NIVEL 3", "Cactus y Piñas");

        levelsPanel.add(level1Button, gbc);
        gbc.gridx++;
        levelsPanel.add(level2Button, gbc);
        gbc.gridx++;
        levelsPanel.add(level3Button, gbc);
        gbc.gridx++;

        // Nivel 4
        level4Button = createLevelButton("NIVEL 4", "Narval y Fuego");
        levelsPanel.add(level4Button, gbc);

        // Botón de volver
        JPanel bottomPanel = new JPanel();
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 30, 0));

        backButton = createIceButton("VOLVER AL MENÚ");
        backButton.setPreferredSize(new Dimension(300, 50));
        bottomPanel.add(backButton);

        add(titlePanel, BorderLayout.NORTH);
        add(levelsPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private JButton createLevelButton(String levelName, String description) {
        JButton button = new JButton();
        button.setLayout(new BorderLayout());
        button.setPreferredSize(new Dimension(200, 180));
        button.setBackground(new Color(180, 220, 255));
        button.setFocusPainted(false);
        button.setBorderPainted(true);

        // Borde con efecto de hielo
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(100, 180, 255), 4),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));

        // Panel interno para el contenido
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);

        JLabel nameLabel = new JLabel(levelName);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 24));
        nameLabel.setForeground(new Color(20, 40, 80));
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel descLabel = new JLabel(description);
        descLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        descLabel.setForeground(new Color(40, 60, 100));
        descLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        contentPanel.add(Box.createVerticalGlue());
        contentPanel.add(nameLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        contentPanel.add(descLabel);
        contentPanel.add(Box.createVerticalGlue());

        button.add(contentPanel, BorderLayout.CENTER);

        // Efecto hover
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(200, 240, 255));
                button.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(150, 200, 255), 4),
                        BorderFactory.createEmptyBorder(15, 15, 15, 15)));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(180, 220, 255));
                button.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(100, 180, 255), 4),
                        BorderFactory.createEmptyBorder(15, 15, 15, 15)));
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

        // Borde con efecto de hielo
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(100, 180, 255), 3),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)));

        // Efecto hover
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

    // Métodos para configurar listeners
    public void setLevel1ButtonListener(ActionListener listener) {
        level1Button.addActionListener(listener);
    }

    public void setLevel2ButtonListener(ActionListener listener) {
        level2Button.addActionListener(listener);
    }

    public void setLevel3ButtonListener(ActionListener listener) {
        level3Button.addActionListener(listener);
    }

    public void setLevel4ButtonListener(ActionListener listener) {
        level4Button.addActionListener(listener);
    }

    public void setBackButtonListener(ActionListener listener) {
        backButton.addActionListener(listener);
    }
}
