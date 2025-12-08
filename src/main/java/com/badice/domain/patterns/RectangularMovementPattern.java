package com.badice.domain.patterns;

import com.badice.domain.entities.Direction;
import com.badice.domain.entities.Enemy;
import com.badice.domain.entities.GameMap;
import com.badice.domain.entities.Player;
import com.badice.domain.interfaces.MovementPattern;

/**
 * Patrón de movimiento con rebote: el enemigo se mueve en línea recta
 * y rota 90° en sentido horario cuando choca con un obstáculo.
 */
public class RectangularMovementPattern implements MovementPattern {
    private static final long serialVersionUID = 1L;
    private Direction currentDirection;

    public RectangularMovementPattern() {
        this.currentDirection = Direction.RIGHT;
    }

    @Override
    public Direction calculateNextDirection(Enemy enemy, GameMap map) {
        var nextPosition = enemy.getPosition().move(currentDirection);

        // Si la siguiente posición está bloqueada, rotar 90° en sentido horario
        if (!isValidMove(nextPosition, map)) {
            rotateClockwise();
        }

        return currentDirection;
    }

    /**
     * Rota la dirección 90° en sentido horario: RIGHT → UP → LEFT → DOWN → RIGHT
     */
    private void rotateClockwise() {
        currentDirection = switch (currentDirection) {
            case RIGHT -> Direction.UP;
            case UP -> Direction.LEFT;
            case LEFT -> Direction.DOWN;
            case DOWN -> Direction.RIGHT;
            default -> Direction.RIGHT;
        };
    }

    @Override
    public void update() {
        // No se necesita actualización de estado
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
                .filter(e -> !(e instanceof Player)) // Ignorar al jugador
                .anyMatch(e -> e.getCollisionPosition().equals(pos));
    }
}
