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
        // Intentar cargar sprite basado en color y dirección
        String color = player.getPlayerColor();
        String direction = player.getCurrentDirection().toString().toLowerCase();
        BufferedImage sprite = resourceManager.getPlayerSprite(color, direction);

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
        if (enemy instanceof TrollEnemy) {
            // Troll: Enemy del nivel 1
            BufferedImage sprite = resourceManager.getEnemySprite("troll", null);

            if (sprite != null) {
                g.drawImage(sprite, x, y, cellSize, cellSize, null);
            } else {
                // Fallback: Red square with eyes
                g.setColor(Color.RED);
                g.fillRect(x + 4, y + 4, cellSize - 8, cellSize - 8);
                g.setColor(Color.DARK_GRAY);
                // Eyes
                g.fillOval(x + 8, y + 10, 6, 6);
                g.fillOval(x + cellSize - 14, y + 10, 6, 6);
            }
        } else if (enemy instanceof SquidEnemy) {
            // Calamar
            BufferedImage sprite = resourceManager.getEnemySprite("squid", null);
            if (sprite != null) {
                g.drawImage(sprite, x, y, cellSize, cellSize, null);
            } else {
                // Fallback: Pink square
                g.setColor(Color.PINK);
                g.fillRect(x + 4, y + 4, cellSize - 8, cellSize - 8);
            }
        } else {
            BufferedImage sprite = resourceManager.getEnemySprite("troll", null);
            // Enemigo básico (fallback)
            if (sprite != null) {
                g.drawImage(sprite, x, y, cellSize, cellSize, null);
            } else {
                g.setColor(Color.RED);
                g.fillRect(x + 4, y + 4, cellSize - 8, cellSize - 8);
                g.setColor(Color.DARK_GRAY);
                // Ojos
                g.fillOval(x + 8, y + 10, 6, 6);
                g.fillOval(x + cellSize - 14, y + 10, 6, 6);
            }
        }
    }

    private void renderFruit(Graphics2D g, Fruit fruit, int x, int y) {
        if (fruit.isCollected()) {
            return;
        }

        BufferedImage sprite = resourceManager.getFruitSprite(fruit.getFruitType());

        if (sprite != null) {
            g.drawImage(sprite, x, y, cellSize, cellSize, null);
        } else {
            // Debug: Log when sprite is not found
            System.out.println("Sprite not found for fruit: " + fruit.getFruitType());
            // Fallback: Colores según tipo de fruta
            Color mainColor, borderColor;
            String type = fruit.getFruitType().toLowerCase();

            switch (type) {
                case "banana":
                case "platano":
                    mainColor = new Color(255, 215, 0); // Amarillo dorado
                    borderColor = new Color(218, 165, 32);
                    break;
                case "uva":
                case "grape":
                    mainColor = new Color(128, 0, 128); // Morado
                    borderColor = new Color(75, 0, 130);
                    break;
                case "cereza":
                case "cherry":
                    mainColor = new Color(220, 20, 60); // Rojo cereza
                    borderColor = new Color(139, 0, 0);
                    break;
                case "pina":
                case "piña":
                case "pineapple":
                    mainColor = new Color(255, 176, 0); // Dorado/naranja
                    borderColor = new Color(255, 140, 0);
                    break;
                case "cactus":
                    mainColor = new Color(34, 139, 34); // Verde bosque
                    borderColor = new Color(0, 100, 0);
                    break;
                default:
                    mainColor = Color.YELLOW;
                    borderColor = Color.ORANGE;
            }

            g.setColor(mainColor);
            g.fillOval(x + 6, y + 6, cellSize - 12, cellSize - 12);
            g.setColor(borderColor);
            g.drawOval(x + 6, y + 6, cellSize - 12, cellSize - 12);
        }
    }

    private void renderIceBlock(Graphics2D g, IceBlock ice, int x, int y) {
        // Aumentar el tamaño del bloque de hielo para que se vea mejor
        int padding = 2; // Reducir padding para que el bloque sea más grande
        g.setColor(new Color(135, 206, 235, 200)); // Sky blue con transparencia
        g.fillRect(x + padding, y + padding, cellSize - (padding * 2), cellSize - (padding * 2));
        g.setColor(new Color(173, 216, 230));
        g.drawRect(x + padding, y + padding, cellSize - (padding * 2) - 1, cellSize - (padding * 2) - 1);

        // Efecto de hielo más visible
        g.setColor(new Color(255, 255, 255, 150));
        g.drawLine(x + padding + 2, y + padding + 2, x + cellSize - padding - 3, y + padding + 2);
        g.drawLine(x + padding + 2, y + padding + 2, x + padding + 2, y + cellSize - padding - 3);
    }

    private void renderBlock(Graphics2D g, Block block, int x, int y) {
        BufferedImage sprite = resourceManager.getBlockSprite(block.getBlockType());

        if (sprite != null) {
            g.drawImage(sprite, x, y, cellSize, cellSize, null);
        } else {
            g.setColor(new Color(0, 107, 201));
            g.fillRect(x, y, cellSize, cellSize);
            g.setColor(new Color(76, 169, 255));
            g.drawRect(x, y, cellSize - 1, cellSize - 1);

            // Textura de ladrillo
            g.setColor(new Color(76, 169, 255));
            g.drawLine(x + cellSize / 2, y, x + cellSize / 2, y + cellSize);
            g.drawLine(x, y + cellSize / 2, x + cellSize, y + cellSize / 2);
        }
    }

    /**
     * Renderiza el fondo/grid del mapa.
     */
    public void renderGrid(Graphics2D g, int width, int height) {
        // NO dibujar fondo sólido aquí - el background se dibuja en GamePanel

        // Grid semi-transparente para que se vea el background
        g.setColor(new Color(255, 255, 255, 30)); // Blanco muy transparente
        for (int x = 0; x <= width; x++) {
            g.drawLine(x * cellSize, 0, x * cellSize, height * cellSize);
        }
        for (int y = 0; y <= height; y++) {
            g.drawLine(0, y * cellSize, width * cellSize, y * cellSize);
        }
    }
}
