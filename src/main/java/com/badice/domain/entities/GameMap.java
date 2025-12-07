package com.badice.domain.entities;

import com.badice.domain.interfaces.Collidable;
import java.io.Serializable;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa el mapa del juego con todas las entidades.
 */
public class GameMap implements Serializable {
    private static final long serialVersionUID = 1L;
    private final int width;
    private final int height;
    private final int cellSize;

    private final List<GameEntity> entities;
    private final List<Block> blocks;
    private final List<IceBlock> iceBlocks;
    private final List<Fruit> fruits;
    private final List<Enemy> enemies;
    private final List<Player> players;

    public GameMap(int width, int height, int cellSize) {
        this.width = width;
        this.height = height;
        this.cellSize = cellSize;

        this.entities = new ArrayList<>();
        this.blocks = new ArrayList<>();
        this.iceBlocks = new ArrayList<>();
        this.fruits = new ArrayList<>();
        this.enemies = new ArrayList<>();
        this.players = new ArrayList<>();
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getCellSize() {
        return cellSize;
    }

    public Player getPlayer() {
        return players.isEmpty() ? null : players.get(0);
    }

    public List<Player> getPlayers() {
        return new ArrayList<>(players);
    }

    public void setPlayer(Player player) {
        this.players.clear();
        addEntity(player);
    }

    public void addPlayer(Player player) {
        addEntity(player);
    }

    public List<GameEntity> getEntities() {
        return new ArrayList<>(entities);
    }

    public List<Block> getBlocks() {
        return new ArrayList<>(blocks);
    }

    public List<IceBlock> getIceBlocks() {
        return new ArrayList<>(iceBlocks);
    }

    public List<Fruit> getFruits() {
        return new ArrayList<>(fruits);
    }

    public List<Enemy> getEnemies() {
        return new ArrayList<>(enemies);
    }

    /**
     * Añade una entidad al mapa.
     */
    public void addEntity(GameEntity entity) {
        if (!entities.contains(entity)) {
            entities.add(entity);

            // Añadir a la lista específica según el tipo
            if (entity instanceof Player) {
                this.players.add((Player) entity);
            } else if (entity instanceof Enemy) {
                enemies.add((Enemy) entity);
            } else if (entity instanceof Fruit) {
                fruits.add((Fruit) entity);
            } else if (entity instanceof IceBlock) {
                iceBlocks.add((IceBlock) entity);
            } else if (entity instanceof Block) {
                blocks.add((Block) entity);
            }
        }
    }

    /**
     * Elimina una entidad del mapa.
     */
    public void removeEntity(GameEntity entity) {
        entities.remove(entity);

        if (entity instanceof Enemy) {
            enemies.remove(entity);
        } else if (entity instanceof Fruit) {
            fruits.remove(entity);
        } else if (entity instanceof IceBlock) {
            iceBlocks.remove(entity);
        } else if (entity instanceof Block) {
            blocks.remove(entity);
        }
    }

    /**
     * Añade un bloque de hielo en la posición especificada.
     */
    public void addIceBlock(Position position) {
        IceBlock ice = new IceBlock(position);
        addEntity(ice);
    }

    /**
     * Elimina un bloque de hielo en la posición especificada.
     */
    public void removeIceBlock(Position position) {
        iceBlocks.stream()
                .filter(ice -> ice.getPosition().equals(position))
                .findFirst()
                .ifPresent(this::removeEntity);
    }

    /**
     * Verifica si una posición está dentro de los límites del mapa.
     */
    public boolean isValidPosition(Position position) {
        return position.getX() >= 0 && position.getX() < width &&
                position.getY() >= 0 && position.getY() < height;
    }

    /**
     * Verifica si una posición está ocupada por una entidad sólida.
     */
    public boolean isPositionBlocked(Position position) {
        return entities.stream()
                .filter(GameEntity::isActive)
                .anyMatch(entity -> entity instanceof Collidable &&
                        ((Collidable) entity).isSolid() &&
                        entity.getPosition().equals(position));
    }

    /**
     * Obtiene la entidad en una posición específica.
     */
    public GameEntity getEntityAt(Position position) {
        return entities.stream()
                .filter(GameEntity::isActive)
                .filter(entity -> entity.getPosition().equals(position))
                .findFirst()
                .orElse(null);
    }

    /**
     * Limpia todas las entidades inactivas del mapa.
     */
    public void cleanupInactiveEntities() {
        entities.removeIf(entity -> !entity.isActive());
        blocks.removeIf(entity -> !entity.isActive());
        iceBlocks.removeIf(entity -> !entity.isActive());
        fruits.removeIf(entity -> !entity.isActive());
        enemies.removeIf(entity -> !entity.isActive());
    }

    /**
     * Actualiza todas las entidades del mapa.
     */
    public void updateAllEntities() {
        for (GameEntity entity : new ArrayList<>(entities)) {
            entity.update();
        }
        cleanupInactiveEntities();
    }

    /**
     * Encuentra una posición aleatoria libre en el mapa.
     * Una posición está libre si está dentro de los límites y no está bloqueada.
     */
    public Position findRandomFreePosition() {
        java.util.Random random = new java.util.Random();
        int maxAttempts = 100; // Evitar bucle infinito

        for (int i = 0; i < maxAttempts; i++) {
            int x = random.nextInt(width);
            int y = random.nextInt(height);
            Position candidate = new Position(x, y);

            if (isValidPosition(candidate) && !isPositionBlocked(candidate)) {
                return candidate;
            }
        }

        // Si no se encuentra una posición libre, retornar null
        return null;
    }

}
