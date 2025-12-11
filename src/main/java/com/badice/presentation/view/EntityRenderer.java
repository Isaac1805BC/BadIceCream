package com.badice.presentation.view;

import com.badice.domain.entities.*;
import com.badice.domain.interfaces.EntityVisitor;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Renderiza entidades específicas del juego.
 * Implementa EntityVisitor para renderizado polimórfico sin instanceof.
 */
public class EntityRenderer implements EntityVisitor {
    private final ResourceManager resourceManager;
    private int cellSize;
    
    // Contexto de renderizado actual
    private Graphics2D currentGraphics;
    private int currentX;
    private int currentY;

    public EntityRenderer(int cellSize) {
        this.resourceManager = ResourceManager.getInstance();
        this.cellSize = cellSize;
    }

    /**
     * Actualiza el tamaño de celda para renderizado dinámico.
     */
    public void setCellSize(int cellSize) {
        this.cellSize = cellSize;
    }

    /**
     * Renderiza una entidad en las coordenadas especificadas.
     */
    public void renderEntity(Graphics2D g, GameEntity entity) {
        if (entity == null || !entity.isActive()) {
            return;
        }

        this.currentGraphics = g;
        Position pos = entity.getPosition();
        this.currentX = pos.getX() * cellSize;
        this.currentY = pos.getY() * cellSize;

        // Despachar al método visit correspondiente
        entity.accept(this);
    }

    // --- Implementación de EntityVisitor ---

    @Override
    public void visit(Player player) {
        String color = player.getPlayerColor();
        String direction = player.getCurrentDirection().toString().toLowerCase();
        BufferedImage sprite = resourceManager.getPlayerSprite(color, direction);

        if (!drawSprite(currentGraphics, sprite, currentX, currentY)) {
            currentGraphics.setColor(Color.CYAN);
            currentGraphics.fillOval(currentX + 2, currentY + 2, cellSize - 4, cellSize - 4);
            currentGraphics.setColor(Color.BLUE);
            currentGraphics.drawOval(currentX + 2, currentY + 2, cellSize - 4, cellSize - 4);
        }
    }

    @Override
    public void visit(TrollEnemy enemy) {
        Direction dir = enemy.getCurrentDirection();
        BufferedImage sprite = switch (dir) {
            case RIGHT -> resourceManager.loadImage("sprites/enemies/TrollCaminandoDerecha.png");
            case LEFT -> resourceManager.loadImage("sprites/enemies/TrollCaminandoIzquierda.png");
            case UP -> resourceManager.loadImage("sprites/enemies/TrollCaminandoHaciaArriba.png");
            case DOWN -> resourceManager.loadImage("sprites/enemies/TrollCaminandoDerecha.png");
            default -> resourceManager.loadImage("sprites/enemies/troll.png");
        };

        if (!drawSprite(currentGraphics, sprite, currentX, currentY)) {
            renderGenericEnemyFallback(enemy, new Color(139, 69, 19));
        }
    }

    @Override
    public void visit(PotEnemy enemy) {
        BufferedImage sprite = resourceManager.loadImage("sprites/enemies/maseta.png");
        if (!drawSprite(currentGraphics, sprite, currentX, currentY)) {
            renderGenericEnemyFallback(enemy, new Color(34, 139, 34));
        }
    }

    @Override
    public void visit(SquidEnemy enemy) {
        BufferedImage sprite;
        if (enemy.isBreakingIce()) {
            sprite = resourceManager.loadImage("sprites/enemies/CalamarRompiendoBloques.png");
        } else {
            Direction dir = enemy.getCurrentDirection();
            sprite = switch (dir) {
                case RIGHT -> resourceManager.loadImage("sprites/enemies/CalamarDerecha.png");
                case LEFT -> resourceManager.loadImage("sprites/enemies/CalamarIzquierda.png");
                case UP -> resourceManager.loadImage("sprites/enemies/CalamarCaminandoHaciaArriba.png");
                case DOWN -> resourceManager.loadImage("sprites/enemies/CalamarDerecha.png");
                default -> resourceManager.loadImage("sprites/enemies/calamar.png");
            };
        }

        if (!drawSprite(currentGraphics, sprite, currentX, currentY)) {
            renderGenericEnemyFallback(enemy, new Color(255, 140, 0));
        }
    }

    @Override
    public void visit(NarvalEnemy enemy) {
        BufferedImage sprite = resourceManager.loadImage("sprites/enemies/NarvalHD.png");
        
        if (!drawSprite(currentGraphics, sprite, currentX, currentY)) {
            Color color = enemy.isCharging() ? new Color(255, 0, 0) : new Color(70, 130, 180);
            renderGenericEnemyFallback(enemy, color);
            
            if (enemy.isCharging()) {
                currentGraphics.setColor(Color.YELLOW);
                currentGraphics.setStroke(new BasicStroke(3));
                int margin = cellSize / 6;
                currentGraphics.drawOval(currentX + margin - 2, currentY + margin - 2, cellSize - 2 * margin + 4, cellSize - 2 * margin + 4);
                currentGraphics.setStroke(new BasicStroke(1));
            }
        }
    }

    @Override
    public void visit(BasicEnemy enemy) {
        // Fallback para enemigo básico genérico
        renderGenericEnemyFallback(enemy, Color.RED);
    }
    
    private void renderGenericEnemyFallback(Enemy enemy, Color color) {
        currentGraphics.setColor(color);
        int margin = cellSize / 6;
        currentGraphics.fillOval(currentX + margin, currentY + margin, cellSize - 2 * margin, cellSize - 2 * margin);
        currentGraphics.setColor(Color.BLACK);
        currentGraphics.drawOval(currentX + margin, currentY + margin, cellSize - 2 * margin, cellSize - 2 * margin);
    }

    // --- Fruits ---

    private void renderCommonFruit(Fruit fruit) {
        if (fruit.isCollected()) return;

        BufferedImage sprite = resourceManager.getFruitSprite(fruit.getFruitType());

        if (!drawSprite(currentGraphics, sprite, currentX, currentY)) {
            Color mainColor = Color.YELLOW;
            Color borderColor = Color.ORANGE;
            String type = fruit.getFruitType().toLowerCase();

            switch (type) {
                case "banana":
                case "platano":
                    mainColor = new Color(255, 215, 0);
                    borderColor = new Color(218, 165, 32);
                    break;
                case "uva":
                case "grape":
                    mainColor = new Color(128, 0, 128);
                    borderColor = new Color(75, 0, 130);
                    break;
                case "cereza":
                case "cherry":
                    mainColor = new Color(220, 20, 60);
                    borderColor = new Color(139, 0, 0);
                    break;
                case "pina":
                case "piña":
                case "pineapple":
                    mainColor = new Color(255, 176, 0);
                    borderColor = new Color(255, 140, 0);
                    break;
                case "cactus":
                    mainColor = new Color(34, 139, 34);
                    borderColor = new Color(0, 100, 0);
                    break;
            }

            currentGraphics.setColor(mainColor);
            currentGraphics.fillOval(currentX + 6, currentY + 6, cellSize - 12, cellSize - 12);
            currentGraphics.setColor(borderColor);
            currentGraphics.drawOval(currentX + 6, currentY + 6, cellSize - 12, cellSize - 12);
        }
    }

    @Override
    public void visit(BasicFruit fruit) { renderCommonFruit(fruit); }
    @Override
    public void visit(CherryFruit fruit) { renderCommonFruit(fruit); }
    @Override
    public void visit(PineappleFruit fruit) { renderCommonFruit(fruit); }
    @Override
    public void visit(CactusFruit fruit) { renderCommonFruit(fruit); }

    // --- Blocks ---

    @Override
    public void visit(IceBlock ice) {
        BufferedImage sprite = null;
        switch (ice.getState()) {
            case INTACT:
                sprite = resourceManager.loadImage("backgrounds/IceBlock1.png");
                break;
            case CRACKED:
                sprite = resourceManager.loadImage("backgrounds/MedianamenteRoto.png");
                break;
            case BROKEN:
                sprite = resourceManager.loadImage("backgrounds/CompletamenteRoto.png");
                break;
        }

        if (!drawSprite(currentGraphics, sprite, currentX, currentY)) {
            int padding = 2;
            currentGraphics.setColor(new Color(135, 206, 235, 200));
            currentGraphics.fillRect(currentX + padding, currentY + padding, cellSize - (padding * 2), cellSize - (padding * 2));
            currentGraphics.setColor(new Color(173, 216, 230));
            currentGraphics.drawRect(currentX + padding, currentY + padding, cellSize - (padding * 2) - 1, cellSize - (padding * 2) - 1);
        }
    }

    @Override
    public void visit(Campfire campfire) {
        BufferedImage sprite = campfire.isLit() 
            ? resourceManager.loadImage("sprites/obstacles/campfireON.png") 
            : resourceManager.loadImage("sprites/obstacles/campfireOF.png");

        if (!drawSprite(currentGraphics, sprite, currentX, currentY)) {
            currentGraphics.setColor(campfire.isLit() ? new Color(255, 69, 0) : new Color(128, 128, 128));
            currentGraphics.fillRect(currentX, currentY, cellSize, cellSize);
            currentGraphics.setColor(Color.BLACK);
            currentGraphics.drawRect(currentX, currentY, cellSize, cellSize);
        }
    }

    @Override
    public void visit(HotTile hotTile) {
        BufferedImage sprite = resourceManager.loadImage("sprites/obstacles/Baldosa_caliente.png");
        if (!drawSprite(currentGraphics, sprite, currentX, currentY)) {
             currentGraphics.setColor(new Color(255, 140, 0));
             currentGraphics.fillRect(currentX, currentY, cellSize, cellSize);
             currentGraphics.setColor(Color.BLACK);
             currentGraphics.drawRect(currentX, currentY, cellSize, cellSize);
        }
    }

    @Override
    public void visit(Block block) {
        // Generic block (Wall, etc.)
        BufferedImage sprite = resourceManager.getBlockSprite(block.getBlockType());
        if (!drawSprite(currentGraphics, sprite, currentX, currentY)) {
            currentGraphics.setColor(new Color(0, 107, 201));
            currentGraphics.fillRect(currentX, currentY, cellSize, cellSize);
            currentGraphics.setColor(Color.BLACK);
            currentGraphics.drawRect(currentX, currentY, cellSize, cellSize);
        }
    }

    // --- Helpers ---

    public void renderGrid(Graphics2D g, int width, int height) {
        g.setColor(new Color(255, 255, 255, 30));
        for (int x = 0; x <= width; x++) {
            g.drawLine(x * cellSize, 0, x * cellSize, height * cellSize);
        }
        for (int y = 0; y <= height; y++) {
            g.drawLine(0, y * cellSize, width * cellSize, y * cellSize);
        }
    }

    private boolean drawSprite(Graphics2D g, BufferedImage sprite, int x, int y) {
        if (sprite != null) {
            g.drawImage(sprite, x, y, cellSize, cellSize, null);
            return true;
        }
        return false;
    }
}
