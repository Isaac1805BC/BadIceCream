package com.badice.presentation.view;

import com.badice.domain.entities.*;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Renderiza entidades específicas del juego.
 */
public class EntityRenderer {
    private final ResourceManager resourceManager;
    private final int cellSize;

    public EntityRenderer(int cellSize) {
        this.resourceManager = ResourceManager.getInstance();
        this.cellSize = cellSize;
    }

    /**
     * Renderiza una entidad en las coordenadas especificadas.
     */
    public void renderEntity(Graphics2D g, GameEntity entity) {
        if (entity == null || !entity.isActive()) {
            return;
        }

        Position pos = entity.getPosition();
        int x = pos.getX() * cellSize;
        int y = pos.getY() * cellSize;

        if (entity instanceof Player) {
            renderPlayer(g, (Player) entity, x, y);
        } else if (entity instanceof Enemy) {
            renderEnemy(g, (Enemy) entity, x, y);
        } else if (entity instanceof Fruit) {
            renderFruit(g, (Fruit) entity, x, y);
        } else if (entity instanceof IceBlock) {
            renderIceBlock(g, (IceBlock) entity, x, y);
        } else if (entity instanceof Block) {
            renderBlock(g, (Block) entity, x, y);
        }
    }

    private void renderPlayer(Graphics2D g, Player player, int x, int y) {
        // Intentar cargar sprite, si no existe usar color
        BufferedImage sprite = resourceManager.getPlayerSprite(player.getCurrentDirection().toString().toLowerCase());

        if (sprite == null) {
            // No hay sprite, usar figura de color
            g.setColor(Color.CYAN);
            g.fillOval(x + 2, y + 2, cellSize - 4, cellSize - 4);
            g.setColor(Color.BLUE);
            g.drawOval(x + 2, y + 2, cellSize - 4, cellSize - 4);
        } else {
            // Hay sprite, dibujarlo
            g.drawImage(sprite, x, y, cellSize, cellSize, null);
        }
    }

    private void renderEnemy(Graphics2D g, Enemy enemy, int x, int y) {
        g.setColor(Color.RED);
        g.fillRect(x + 4, y + 4, cellSize - 8, cellSize - 8);
        g.setColor(Color.DARK_GRAY);

        // Ojos
        g.fillOval(x + 8, y + 10, 6, 6);
        g.fillOval(x + cellSize - 14, y + 10, 6, 6);
    }

    private void renderFruit(Graphics2D g, Fruit fruit, int x, int y) {
        if (fruit.isCollected()) {
            return;
        }

        g.setColor(Color.YELLOW);
        g.fillOval(x + 6, y + 6, cellSize - 12, cellSize - 12);
        g.setColor(Color.ORANGE);
        g.drawOval(x + 6, y + 6, cellSize - 12, cellSize - 12);
    }

    private void renderIceBlock(Graphics2D g, IceBlock ice, int x, int y) {
        g.setColor(new Color(135, 206, 235, 200)); // Sky blue con transparencia
        g.fillRect(x, y, cellSize, cellSize);
        g.setColor(new Color(173, 216, 230));
        g.drawRect(x, y, cellSize - 1, cellSize - 1);

        // Efecto de hielo
        g.setColor(new Color(255, 255, 255, 100));
        g.drawLine(x + 2, y + 2, x + cellSize - 3, y + 2);
        g.drawLine(x + 2, y + 2, x + 2, y + cellSize - 3);
    }

    private void renderBlock(Graphics2D g, Block block, int x, int y) {
        g.setColor(new Color(139, 69, 19)); // Brown
        g.fillRect(x, y, cellSize, cellSize);
        g.setColor(new Color(101, 67, 33));
        g.drawRect(x, y, cellSize - 1, cellSize - 1);

        // Textura de ladrillo
        g.setColor(new Color(160, 82, 45));
        g.drawLine(x + cellSize / 2, y, x + cellSize / 2, y + cellSize);
        g.drawLine(x, y + cellSize / 2, x + cellSize, y + cellSize / 2);
    }

    /**
     * Renderiza el fondo/grid del mapa.
     */
    public void renderGrid(Graphics2D g, int width, int height) {
        g.setColor(new Color(20, 20, 40));
        g.fillRect(0, 0, width * cellSize, height * cellSize);

        // Grid
        g.setColor(new Color(40, 40, 60));
        for (int x = 0; x <= width; x++) {
            g.drawLine(x * cellSize, 0, x * cellSize, height * cellSize);
        }
        for (int y = 0; y <= height; y++) {
            g.drawLine(0, y * cellSize, width * cellSize, y * cellSize);
        }
    }
}
