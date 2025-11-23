package com.badice.presentation.view;

import com.badice.domain.entities.GameEntity;
import com.badice.domain.entities.GameMap;
import com.badice.domain.entities.Player;
import com.badice.domain.services.GameEngine;
import com.badice.domain.states.PausedState;

import javax.swing.*;
import java.awt.*;

/**
 * Panel principal donde se dibuja el juego.
 */
public class GamePanel extends JPanel {
    private final GameEngine gameEngine;
    private final EntityRenderer entityRenderer;
    private final HUDRenderer hudRenderer;

    public GamePanel(GameEngine gameEngine) {
        this.gameEngine = gameEngine;
        this.entityRenderer = new EntityRenderer(32); // 32px por celda
        this.hudRenderer = new HUDRenderer();

        setBackground(Color.BLACK);
        setFocusable(true);
        setPreferredSize(new Dimension(800, 600));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        GameMap map = gameEngine.getCurrentMap();
        if (map == null) {
            renderNoMapMessage(g2d);
            return;
        }

        // Calcular offset para centrar el grid horizontalmente
        int mapPixelWidth = map.getWidth() * 32; // 32 = cellSize
        int horizontalOffset = (getWidth() - mapPixelWidth) / 2;

        // Offset vertical para dejar espacio al HUD
        int verticalOffset = 50;

        // Aplicar offset para centrar
        g2d.translate(horizontalOffset, verticalOffset);

        // Renderizar fondo/grid
        entityRenderer.renderGrid(g2d, map.getWidth(), map.getHeight());

        // Renderizar todas las entidades
        for (GameEntity entity : map.getEntities()) {
            entityRenderer.renderEntity(g2d, entity);
        }

        // Volver al origen
        g2d.translate(-horizontalOffset, -verticalOffset);

        // Renderizar HUD
        Player player = gameEngine.getPlayer();
        long elapsedTime = gameEngine.getElapsedTime();
        hudRenderer.render(g2d, player, gameEngine.getScoreService(), elapsedTime, getWidth());

        // Renderizar overlay de pausa
        if (gameEngine.getStateManager().isInState(PausedState.class)) {
            renderPauseOverlay(g2d);
        }
    }

    private void renderNoMapMessage(Graphics2D g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 24));
        String message = "No hay mapa cargado";
        FontMetrics metrics = g.getFontMetrics();
        int x = (getWidth() - metrics.stringWidth(message)) / 2;
        int y = getHeight() / 2;
        g.drawString(message, x, y);
    }

    private void renderPauseOverlay(Graphics2D g) {
        // Semi-transparente overlay
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRect(0, 0, getWidth(), getHeight());

        // Mensaje de pausa
        hudRenderer.renderCenteredMessage(g, "PAUSA", getWidth(), getHeight());

        g.setFont(new Font("Arial", Font.PLAIN, 14));
        g.setColor(Color.LIGHT_GRAY);
        String instruction = "Presiona P para continuar";
        FontMetrics metrics = g.getFontMetrics();
        int x = (getWidth() - metrics.stringWidth(instruction)) / 2;
        g.drawString(instruction, x, getHeight() / 2 + 30);
    }

    /**
     * Actualiza el panel (fuerza repintado).
     */
    public void update() {
        repaint();
    }
}
