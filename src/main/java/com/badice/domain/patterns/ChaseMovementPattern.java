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

        // Priorizar el eje con mayor distancia
        if (Math.abs(dx) > Math.abs(dy)) {
            if (dx > 0) return Direction.RIGHT;
            else return Direction.LEFT;
        } else {
            if (dy > 0) return Direction.DOWN;
            else return Direction.UP;
        }
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
