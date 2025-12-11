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
                if (entity instanceof Fruit) {
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
     * @return true si el jugador murió por una colisión fatal (campfire), false de lo contrario
     */
    public boolean handlePlayerCollisions(Player player, GameMap map, ScoreService scoreService) {
        List<GameEntity> collisions = detectPlayerCollisions(player, map);
        boolean playerDied = false;

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
            } else if (entity instanceof Campfire) {
                Campfire campfire = (Campfire) entity;
                if (campfire.isLit()) {
                    playerDied = handlePlayerCampfireCollision(player, campfire);
                }
            }
        }

        // ADICIONAL: Verificar fogatas en la posición actual del jugador
        // (para cuando el jugador está quieto sobre una fogata)
        for (GameEntity entity : map.getEntities()) {
            if (entity instanceof Campfire && entity.isActive()) {
                Campfire campfire = (Campfire) entity;
                if (campfire.isLit() && campfire.getPosition().equals(player.getPosition())) {
                    playerDied = handlePlayerCampfireCollision(player, campfire);
                }
            }
        }
        
        return playerDied;
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

    /**
     * Maneja la colisión entre jugador y fogata encendida.
     * @return true para indicar que el jugador murió
     */
    private boolean handlePlayerCampfireCollision(Player player, Campfire campfire) {
        // El jugador muere instantáneamente
        System.out.println("¡Jugador colisionó con fogata!");
        return true; // Indicar que ocurrió una muerte fatal
    }
}
