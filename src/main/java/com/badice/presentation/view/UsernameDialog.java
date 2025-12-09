package com.badice.presentation.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Diálogo para ingresar el nombre de usuario antes de iniciar el juego.
 */
public class UsernameDialog extends JDialog {
    private JTextField usernameField;
    private JTextField username2Field; // Para segundo jugador
    private String player1Username;
    private String player2Username;
    private boolean confirmed = false;
    private boolean twoPlayers = false;

    public UsernameDialog(Frame parent, boolean twoPlayers) {
        super(parent, "Ingresa tu nombre", true);
        this.twoPlayers = twoPlayers;
        initializeComponents();
        setLocationRelativeTo(parent);
    }

    private void initializeComponents() {
        setLayout(new BorderLayout(10, 10));
        setSize(400, twoPlayers ? 250 : 200);
        setResizable(false);

        // Panel principal con padding
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        mainPanel.setBackground(new Color(180, 220, 255));

        // Título
        JLabel titleLabel = new JLabel(twoPlayers ? "Nombres de Jugadores" : "Nombre de Jugador");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(new Color(20, 40, 80));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Panel para jugador 1
        JPanel player1Panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        player1Panel.setOpaque(false);
        JLabel label1 = new JLabel(twoPlayers ? "Jugador 1:" : "Nombre:");
        label1.setFont(new Font("Arial", Font.BOLD, 14));
        label1.setForeground(new Color(20, 40, 80));
        usernameField = new JTextField(15);
        usernameField.setFont(new Font("Arial", Font.PLAIN, 14));
        player1Panel.add(label1);
        player1Panel.add(usernameField);
        mainPanel.add(player1Panel);

        // Panel para jugador 2 (solo si es modo 2 jugadores)
        if (twoPlayers) {
            mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            JPanel player2Panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            player2Panel.setOpaque(false);
            JLabel label2 = new JLabel("Jugador 2:");
            label2.setFont(new Font("Arial", Font.BOLD, 14));
            label2.setForeground(new Color(20, 40, 80));
            username2Field = new JTextField(15);
            username2Field.setFont(new Font("Arial", Font.PLAIN, 14));
            player2Panel.add(label2);
            player2Panel.add(username2Field);
            mainPanel.add(player2Panel);
        }

        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Panel de botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setOpaque(false);

        JButton confirmButton = createStyledButton("Confirmar");
        JButton cancelButton = createStyledButton("Cancelar");

        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                player1Username = usernameField.getText().trim();

                if (player1Username.isEmpty()) {
                    JOptionPane.showMessageDialog(UsernameDialog.this,
                            "Por favor ingresa un nombre para el Jugador 1",
                            "Nombre requerido",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }

                if (twoPlayers) {
                    player2Username = username2Field.getText().trim();
                    if (player2Username.isEmpty()) {
                        JOptionPane.showMessageDialog(UsernameDialog.this,
                                "Por favor ingresa un nombre para el Jugador 2",
                                "Nombre requerido",
                                JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                }

                confirmed = true;
                dispose();
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                confirmed = false;
                dispose();
            }
        });

        buttonPanel.add(confirmButton);
        buttonPanel.add(cancelButton);
        mainPanel.add(buttonPanel);

        add(mainPanel, BorderLayout.CENTER);

        // Enter key para confirmar
        getRootPane().setDefaultButton(confirmButton);
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setForeground(new Color(20, 40, 80));
        button.setBackground(new Color(200, 240, 255));
        button.setFocusPainted(false);
        button.setBorderPainted(true);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(100, 180, 255), 2),
                BorderFactory.createEmptyBorder(5, 15, 5, 15)));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(220, 250, 255));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(200, 240, 255));
            }
        });

        return button;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public String getPlayer1Username() {
        return player1Username;
    }

    public String getPlayer2Username() {
        return player2Username;
    }
}
