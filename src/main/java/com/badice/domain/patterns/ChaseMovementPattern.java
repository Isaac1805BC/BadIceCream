package com.badice.domain.patterns;

import com.badice.domain.entities.Direction;
import com.badice.domain.entities.Enemy;
import com.badice.domain.entities.GameMap;
import com.badice.domain.entities.Player;
import com.badice.domain.entities.Position;
import com.badice.domain.interfaces.MovementPattern;

/**
 * Patrón de movimiento que persigue al jugador.
 */
public class ChaseMovementPattern implements MovementPattern {

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

        // Intentar moverse en el eje con mayor distancia primero
        if (Math.abs(dx) > Math.abs(dy)) {
            // Preferencia horizontal
            if (canMove(enemy, horizontalDir, map)) {
                return horizontalDir;
            } else if (dy != 0 && canMove(enemy, verticalDir, map)) {
                return verticalDir;
            }
        } else {
            // Preferencia vertical
            if (canMove(enemy, verticalDir, map)) {
                return verticalDir;
            } else if (dx != 0 && canMove(enemy, horizontalDir, map)) {
                return horizontalDir;
            }
        }

        return Direction.NONE;
    }

    private boolean canMove(Enemy enemy, Direction dir, GameMap map) {
        Position nextPos = enemy.getPosition().move(dir);
        return map.isValidPosition(nextPos) && !map.isPositionBlocked(nextPos);
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
