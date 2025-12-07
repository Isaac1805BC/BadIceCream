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
    private static final long serialVersionUID = 1L;
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
        // Si la siguiente posición está bloqueada (por algo que no sea jugador) o fuera
        // de límites, cambiar de dirección
        if (!isValidMove(nextPosition, map)) {
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

    private boolean isValidMove(com.badice.domain.entities.Position pos, GameMap map) {
        if (!map.isValidPosition(pos)) {
            return false;
        }

        // Verificar si está bloqueado por algo que NO sea un jugador
        return !map.getEntities().stream()
                .filter(e -> e.isActive() && e instanceof com.badice.domain.interfaces.Collidable)
                .map(e -> (com.badice.domain.interfaces.Collidable) e)
                .filter(com.badice.domain.interfaces.Collidable::isSolid)
                .filter(e -> !(e instanceof com.badice.domain.entities.Player)) // Ignorar al jugador
                .anyMatch(e -> e.getCollisionPosition().equals(pos));
    }
}
