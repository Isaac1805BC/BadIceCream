package com.badice.presentation.view;

import com.badice.domain.enums.BotProfile;
import com.badice.domain.enums.GameMode;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

public class BotProfileSelectionPanel extends JPanel {
    private JComboBox<BotProfile> bot1ProfileCombo;
    private JComboBox<BotProfile> bot2ProfileCombo;
    private JButton nextButton;
    private JButton backButton;
    private BufferedImage backgroundImage;
    private ResourceManager resourceManager;
    private GameMode gameMode;

    public BotProfileSelectionPanel(GameMode mode) {
        this.gameMode = mode;
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
        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(40, 0, 20, 0));
        
        JLabel titleLabel = new JLabel("CONFIGURAR BOTS");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        titleLabel.setForeground(new Color(16, 25, 34));
        titlePanel.add(titleLabel);

        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Bot 1 Selection (Always visible if PVM or MVM)
        if (gameMode == GameMode.PVM || gameMode == GameMode.MVM) {
            gbc.gridx = 0; gbc.gridy = 0;
            JLabel label1 = createLabel("Bot 1 (Rojo):");
            centerPanel.add(label1, gbc);
            
            gbc.gridx = 1;
            bot1ProfileCombo = new JComboBox<>(BotProfile.values());
            styleComboBox(bot1ProfileCombo);
            centerPanel.add(bot1ProfileCombo, gbc);
        }

        // Bot 2 Selection (Only visible if MVM)
        if (gameMode == GameMode.MVM) {
            gbc.gridx = 0; gbc.gridy = 1;
            JLabel label2 = createLabel("Bot 2 (Marrón):");
            centerPanel.add(label2, gbc);
            
            gbc.gridx = 1;
            bot2ProfileCombo = new JComboBox<>(BotProfile.values());
            styleComboBox(bot2ProfileCombo);
            centerPanel.add(bot2ProfileCombo, gbc);
        }

        JPanel bottomPanel = new JPanel();
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 30, 0));

        backButton = createIceButton("VOLVER");
        nextButton = createIceButton("CONTINUAR");
        
        bottomPanel.add(backButton);
        bottomPanel.add(Box.createHorizontalStrut(20));
        bottomPanel.add(nextButton);

        add(titlePanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 24));
        label.setForeground(Color.WHITE);
        // Sombra negra
        label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
        return label;
    }

    private void styleComboBox(JComboBox<BotProfile> combo) {
        combo.setFont(new Font("Arial", Font.PLAIN, 18));
        combo.setPreferredSize(new Dimension(200, 40));
    }

    private JButton createIceButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 20));
        button.setForeground(new Color(20, 40, 80));
        button.setBackground(new Color(180, 220, 255));
        button.setFocusPainted(false);
        button.setBorderPainted(true);
        button.setPreferredSize(new Dimension(200, 50));
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(100, 180, 255), 3),
            BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));
        return button;
    }

    public BotProfile getBot1Profile() {
        return bot1ProfileCombo != null ? (BotProfile) bot1ProfileCombo.getSelectedItem() : BotProfile.HUNGRY;
    }

    public BotProfile getBot2Profile() {
        return bot2ProfileCombo != null ? (BotProfile) bot2ProfileCombo.getSelectedItem() : BotProfile.HUNGRY;
    }

    public void setNextButtonListener(ActionListener listener) {
        nextButton.addActionListener(listener);
    }

    public void setBackButtonListener(ActionListener listener) {
        backButton.addActionListener(listener);
    }
}
