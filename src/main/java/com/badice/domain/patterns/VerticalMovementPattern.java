package com.badice.domain.patterns;

import com.badice.domain.entities.Direction;
import com.badice.domain.entities.Enemy;
import com.badice.domain.entities.GameMap;
import com.badice.domain.interfaces.MovementPattern;

/**
 * Patrón de movimiento vertical: el enemigo se mueve arriba y abajo
 * y cambia de dirección al encontrar un obstáculo.
 */
public class VerticalMovementPattern implements MovementPattern {
    private static final long serialVersionUID = 1L;
    private Direction currentDirection;

    public VerticalMovementPattern() {
        this.currentDirection = Direction.DOWN;
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
        this.currentDirection = Direction.DOWN;
    }
}
