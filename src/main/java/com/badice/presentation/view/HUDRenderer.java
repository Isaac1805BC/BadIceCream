package com.badice.presentation.view;

import com.badice.domain.entities.Player;
import com.badice.domain.services.ScoreService;

import java.awt.*;

/**
 * Renderiza el HUD (vidas, puntos, tiempo) del juego.
 */
public class HUDRenderer {
    private final Font hudFont;
    private final Font titleFont;

    public HUDRenderer() {
        this.hudFont = new Font("Arial", Font.BOLD, 16);
        this.titleFont = new Font("Arial", Font.BOLD, 20);
    }

    /**
     * Renderiza el HUD en la parte superior del panel de juego.
     */
    public void render(Graphics2D g, Player player, ScoreService scoreService, long elapsedTime, int panelWidth) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Fondo del HUD
        g.setColor(new Color(0, 0, 0, 180));
        g.fillRect(0, 0, panelWidth, 40);

        // Línea divisoria
        g.setColor(Color.CYAN);
        g.drawLine(0, 40, panelWidth, 40);

        g.setFont(hudFont);

        // Vidas
        if (player != null) {
            g.setColor(Color.RED);
            int heartX = 10;
            for (int i = 0; i < player.getLives(); i++) {
                drawHeart(g, heartX, 12);
                heartX += 25;
            }
        }

        // Puntuación
        g.setColor(Color.YELLOW);
        String scoreText = "PUNTOS: " + scoreService.getCurrentScore();
        g.drawString(scoreText, panelWidth / 2 - 80, 25);

        // Nivel
        g.setColor(Color.GREEN);
        String levelText = "NIVEL: " + scoreService.getCurrentLevel();
        g.drawString(levelText, panelWidth - 150, 25);

        // Frutas recolectadas
        g.setColor(Color.ORANGE);
        String fruitsText = scoreService.getFruitsCollected() + "/" + scoreService.getTotalFruits();
        g.drawString(fruitsText, panelWidth / 2 + 50, 25);

        // Tiempo Restante
        g.setColor(Color.CYAN);
        // Calculamos la cuenta regresiva aquí basándonos en el límite conocido.
        long remaining = Math.max(0, 3 * 60 * 1000 - elapsedTime);
        
        String timeText = formatTime(remaining);
        g.drawString(timeText, 10, panelWidth - 100);
    }

    private void drawHeart(Graphics2D g, int x, int y) {
        g.fillOval(x, y, 10, 10);
        g.fillOval(x + 8, y, 10, 10);
        int[] xPoints = { x, x + 18, x + 9 };
        int[] yPoints = { y + 8, y + 8, y + 20 };
        g.fillPolygon(xPoints, yPoints, 3);
    }

    private String formatTime(long milliseconds) {
        long seconds = milliseconds / 1000;
        long minutes = seconds / 60;
        seconds = seconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    /**
     * Renderiza un mensaje centrado en el panel.
     */
    public void renderCenteredMessage(Graphics2D g, String message, int panelWidth, int panelHeight) {
        g.setFont(titleFont);
        FontMetrics metrics = g.getFontMetrics();
        int x = (panelWidth - metrics.stringWidth(message)) / 2;
        int y = panelHeight / 2;

        // Sombra
        g.setColor(Color.BLACK);
        g.drawString(message, x + 2, y + 2);

        // Texto
        g.setColor(Color.WHITE);
        g.drawString(message, x, y);
    }
}
