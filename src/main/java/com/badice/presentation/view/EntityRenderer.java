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
        BufferedImage sprite = null;

        // Determinar qué sprite cargar basado en el tipo de enemigo
        if (enemy instanceof TrollEnemy) {
            sprite = resourceManager.loadImage("sprites/enemies/troll.png");
        } else if (enemy instanceof PotEnemy) {
            sprite = resourceManager.loadImage("sprites/enemies/maseta.png");
        } else if (enemy instanceof SquidEnemy) {
            sprite = resourceManager.loadImage("sprites/enemies/calamar.png");
        } else if (enemy instanceof NarvalEnemy) {
            // NUEVO: Sprite para Narval
            sprite = resourceManager.loadImage("sprites/enemies/Narval.png");
        }

        if (sprite != null) {
            // Escalar sprite al tamaño de la celda
            Image scaledSprite = sprite.getScaledInstance(cellSize, cellSize, Image.SCALE_SMOOTH);
            g.drawImage(scaledSprite, x, y, null);
        } else {
            // Fallback: renderizado básico
            if (enemy instanceof TrollEnemy) {
                g.setColor(new Color(139, 69, 19)); // Marrón para Troll
            } else if (enemy instanceof PotEnemy) {
                g.setColor(new Color(34, 139, 34)); // Verde para Maceta
            } else if (enemy instanceof SquidEnemy) {
                g.setColor(new Color(255, 140, 0)); // Naranja para Calamar
            } else if (enemy instanceof NarvalEnemy) {
                // NUEVO: Color para Narval
                NarvalEnemy narval = (NarvalEnemy) enemy;
                if (narval.isCharging()) {
                    g.setColor(new Color(255, 0, 0)); // Rojo cuando embiste
                } else {
                    g.setColor(new Color(70, 130, 180)); // Azul acero normal
                }
            } else {
                g.setColor(Color.RED);
            }

            // Dibujar círculo para enemigo
            int margin = cellSize / 6;
            g.fillOval(x + margin, y + margin, cellSize - 2 * margin, cellSize - 2 * margin);

            // Borde negro
            g.setColor(Color.BLACK);
            g.drawOval(x + margin, y + margin, cellSize - 2 * margin, cellSize - 2 * margin);

            // Indicador visual para Narval cargando
            if (enemy instanceof NarvalEnemy && ((NarvalEnemy) enemy).isCharging()) {
                g.setColor(Color.YELLOW);
                g.setStroke(new BasicStroke(3));
                g.drawOval(x + margin - 2, y + margin - 2, cellSize - 2 * margin + 4, cellSize - 2 * margin + 4);
                g.setStroke(new BasicStroke(1));
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
        int padding = 2;
        g.setColor(new Color(135, 206, 235, 200)); // Sky blue con transparencia
        g.fillRect(x + padding, y + padding, cellSize - (padding * 2), cellSize - (padding * 2));
        g.setColor(new Color(173, 216, 230));
        g.drawRect(x + padding, y + padding, cellSize - (padding * 2) - 1, cellSize - (padding * 2) - 1);

        // Efecto de hielo
        g.setColor(new Color(255, 255, 255, 150));
        g.drawLine(x + padding + 2, y + padding + 2, x + cellSize - padding - 3, y + padding + 2);
        g.drawLine(x + padding + 2, y + padding + 2, x + padding + 2, y + cellSize - padding - 3);
    }

    private void renderBlock(Graphics2D g, Block block, int x, int y) {
        BufferedImage sprite = null;

        // Determinar qué sprite cargar basado en el tipo de bloque
        if (block instanceof Campfire) {
            // NUEVO: Sprite para Fogata
            sprite = resourceManager.loadImage("sprites/obstacles/Fogata.png");
        } else if (block instanceof HotTile) {
            // NUEVO: Sprite para Baldosa Caliente
            sprite = resourceManager.loadImage("sprites/obstacles/Baldosa_caliente.png");
        } else {
            // Intentar cargar sprite de bloque normal
            sprite = resourceManager.getBlockSprite(block.getBlockType());
        }

        if (sprite != null) {
            // Escalar sprite al tamaño de la celda
            Image scaledSprite = sprite.getScaledInstance(cellSize, cellSize, Image.SCALE_SMOOTH);
            g.drawImage(scaledSprite, x, y, null);
        } else {
            // Fallback: renderizado básico según el tipo
            if (block instanceof Campfire) {
                Campfire campfire = (Campfire) block;
                if (campfire.isLit()) {
                    // Fogata encendida - rojo/naranja
                    g.setColor(new Color(255, 69, 0));
                } else {
                    // Fogata apagada - gris
                    g.setColor(new Color(128, 128, 128));
                }
            } else if (block instanceof HotTile) {
                // Baldosa caliente - naranja brillante
                g.setColor(new Color(255, 140, 0));
            } else {
                // Bloque normal - azul
                g.setColor(new Color(0, 107, 201));
            }

            g.fillRect(x, y, cellSize, cellSize);
            g.setColor(Color.BLACK);
            g.drawRect(x, y, cellSize, cellSize);
        }
    }

    /**
     * Renderiza el fondo/grid del mapa.
     */
    public void renderGrid(Graphics2D g, int width, int height) {
        // Grid semi-transparente
        g.setColor(new Color(255, 255, 255, 30)); // Blanco muy transparente
        for (int x = 0; x <= width; x++) {
            g.drawLine(x * cellSize, 0, x * cellSize, height * cellSize);
        }
        for (int y = 0; y <= height; y++) {
            g.drawLine(0, y * cellSize, width * cellSize, y * cellSize);
        }
    }
}
