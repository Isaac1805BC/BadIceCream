package com.badice.domain.patterns;

import com.badice.domain.entities.Direction;
import com.badice.domain.entities.Enemy;
import com.badice.domain.entities.GameMap;
import com.badice.domain.interfaces.MovementPattern;

/**
 * Patrón de movimiento horizontal: el enemigo se mueve de izquierda a derecha
 * y cambia de dirección al encontrar un obstáculo.
 */
public class HorizontalMovementPattern implements MovementPattern {
    private Direction currentDirection;

    public HorizontalMovementPattern() {
        this.currentDirection = Direction.RIGHT;
    }

    @Override
    public Direction calculateNextDirection(Enemy enemy, GameMap map) {
        // Calcular la siguiente posición
        var nextPosition = enemy.getPosition().move(currentDirection);

        // Si la siguiente posición está bloqueada o fuera de límites, cambiar de
        // dirección
        if (!map.isValidPosition(nextPosition) || map.isPositionBlocked(nextPosition)) {
            currentDirection = currentDirection.opposite();
        }

        return currentDirection;
    }

    @Override
    public void update() {
        // No se necesita actualización de estado para este patrón
    }

    @Override
    public void reset() {
        this.currentDirection = Direction.RIGHT;
    }
}
