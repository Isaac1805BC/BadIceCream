package com.badice.domain.patterns;

import com.badice.domain.entities.Direction;
import com.badice.domain.entities.Enemy;
import com.badice.domain.entities.GameMap;
import com.badice.domain.entities.Player;
import com.badice.domain.entities.Position;
import com.badice.domain.entities.IceBlock;
import com.badice.domain.entities.SquidEnemy;
import com.badice.domain.interfaces.MovementPattern;

/**
 * Patrón de movimiento que persigue al jugador.
 */
public class ChaseMovementPattern implements MovementPattern {
    private static final long serialVersionUID = 1L;

    @Override
    public Direction calculateNextDirection(Enemy enemy, GameMap map) {
        Player player = map.getPlayer();
        if (player == null || !player.isActive()) {
            return Direction.NONE;
        }

        Position enemyPos = enemy.getPosition();
        Position playerPos = player.getPosition();

        int dx = playerPos.getX() - enemyPos.getX();
        int dy = playerPos.getY() - enemyPos.getY();

        // Determinar direcciones preferidas
        Direction horizontalDir = (dx > 0) ? Direction.RIGHT : Direction.LEFT;
        Direction verticalDir = (dy > 0) ? Direction.DOWN : Direction.UP;

        // Si es un SquidEnemy, verificar y romper hielos en el camino
        boolean isSquid = enemy instanceof SquidEnemy;

        // Intentar moverse en el eje con mayor distancia primero
        if (Math.abs(dx) > Math.abs(dy)) {
            // Preferencia horizontal
            if (canMove(enemy, horizontalDir, map, isSquid)) {
                return horizontalDir;
            } else if (dy != 0 && canMove(enemy, verticalDir, map, isSquid)) {
                return verticalDir;
            }
        } else {
            // Preferencia vertical
            if (canMove(enemy, verticalDir, map, isSquid)) {
                return verticalDir;
            } else if (dx != 0 && canMove(enemy, horizontalDir, map, isSquid)) {
                return horizontalDir;
            }
        }

        return Direction.NONE;
    }

    private boolean canMove(Enemy enemy, Direction dir, GameMap map, boolean isSquid) {
        Position nextPos = enemy.getPosition().move(dir);

        if (!map.isValidPosition(nextPos)) {
            return false;
        }

        // Si es un calamar, verificar si hay hielo y romperlo
        if (isSquid) {
            boolean hasIce = map.getEntities().stream()
                    .filter(e -> e instanceof IceBlock)
                    .filter(e -> e.isActive())
                    .anyMatch(e -> e.getPosition().equals(nextPos));

            if (hasIce) {
                // Romper el hielo
                SquidEnemy squid = (SquidEnemy) enemy;
                squid.breakIceAt(nextPos, map);
                return true; // Ahora puede moverse a esa posición
            }
        }

        // Verificar si está bloqueado por algo que NO sea un jugador ni hielo
        return !map.getEntities().stream()
                .filter(e -> e.isActive() && e instanceof com.badice.domain.interfaces.Collidable)
                .filter(e -> !(e instanceof IceBlock) || !isSquid) // Ignorar hielos SOLO si soy Squid (ya revisé
                                                                   // arriba)
                .map(e -> (com.badice.domain.interfaces.Collidable) e)
                .filter(com.badice.domain.interfaces.Collidable::isSolid)
                .filter(e -> !(e instanceof Player)) // Ignorar al jugador
                .anyMatch(e -> e.getCollisionPosition().equals(nextPos));
    }

    @Override
    public void update() {
        // No requiere estado interno por ahora
    }

    @Override
    public void reset() {
        // No hay estado que resetear
    }
}