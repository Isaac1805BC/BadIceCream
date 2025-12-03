package com.badice.domain.services;

import com.badice.domain.entities.*;
import com.badice.domain.interfaces.Collidable;

import java.util.ArrayList;
import java.util.List;

/**
 * Servicio que detecta y maneja colisiones entre entidades.
 */
public class CollisionDetector {

    /**
     * Verifica si una posición colisiona con una entidad sólida.
     */
    public boolean willCollideWithSolid(Position position, GameMap map) {
        return map.getEntities().stream()
                .filter(GameEntity::isActive)
                .filter(entity -> entity instanceof Collidable)
                .map(entity -> (Collidable) entity)
                .filter(Collidable::isSolid)
                .anyMatch(collidable -> collidable.getCollisionPosition().equals(position));
    }

    /**
     * Detecta todas las colisiones del jugador con otras entidades.
     */
    public List<GameEntity> detectPlayerCollisions(Player player, GameMap map) {
        List<GameEntity> collisions = new ArrayList<>();

        for (GameEntity entity : map.getEntities()) {
            if (!entity.isActive() || entity.equals(player)) {
                continue;
            }

            if (entity instanceof Collidable) {
                Collidable collidable = (Collidable) entity;
                // Debug: Verificar distancia
                if (entity instanceof Fruit) {
                    // System.out.println("Verificando colisión con fruta en " +
                    // entity.getPosition() + " Jugador en " + player.getPosition());
                    if (entity.getPosition().equals(player.getPosition())) {
                    }
                }

                if (collidable.collidesWith(player)) {
                    collisions.add(entity);
                }
            }
        }

        return collisions;
    }

    /**
     * Detecta colisiones entre un enemigo y el jugador.
     */
    public boolean detectEnemyPlayerCollision(Enemy enemy, Player player) {
        return enemy.isActive() &&
                player.isActive() &&
                enemy.getPosition().equals(player.getPosition());
    }

    /**
     * Maneja las colisiones del jugador.
     */
    public void handlePlayerCollisions(Player player, GameMap map, ScoreService scoreService) {
        List<GameEntity> collisions = detectPlayerCollisions(player, map);

        for (GameEntity entity : collisions) {
            if (entity instanceof Fruit) {
                Fruit fruit = (Fruit) entity;
                if (!fruit.isCollected()) {
                    fruit.collect();
                    int points = fruit.getPoints();
                    scoreService.addFruitScore(points);
                    player.addScore(points); // Añadir puntos al jugador individual
                }
            } else if (entity instanceof Enemy) {
                handlePlayerEnemyCollision(player, (Enemy) entity);
            }
        }
    }

    /**
     * Maneja la colisión entre jugador y enemigo.
     */
    private void handlePlayerEnemyCollision(Player player, Enemy enemy) {
        // El jugador pierde una vida
        player.loseLife();

        // Podrías resetear la posición del jugador aquí
        System.out.println("¡Colisión con enemigo! Vidas restantes: " + player.getLives());
    }

    /**
     * Obtiene todas las entidades en una posición específica.
     */
    public List<GameEntity> getEntitiesAt(Position position, GameMap map) {
        List<GameEntity> entities = new ArrayList<>();

        for (GameEntity entity : map.getEntities()) {
            if (entity.isActive() && entity.getPosition().equals(position)) {
                entities.add(entity);
            }
        }

        return entities;
    }
}
