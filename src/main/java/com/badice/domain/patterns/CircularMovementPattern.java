package com.badice.domain.patterns;

import com.badice.domain.entities.Direction;
import com.badice.domain.entities.Enemy;
import com.badice.domain.entities.GameMap;
import com.badice.domain.interfaces.MovementPattern;

/**
 * Patrón de movimiento circular: el enemigo se mueve en un patrón circular
 * (derecha -> abajo -> izquierda -> arriba -> repetir).
 */
public class CircularMovementPattern implements MovementPattern {
    private static final long serialVersionUID = 1L;
    private static final Direction[] PATTERN = {
            Direction.RIGHT, Direction.DOWN, Direction.LEFT, Direction.UP
    };

    private int currentIndex;
    private int stepsTaken;
    private final int stepsPerDirection;

    public CircularMovementPattern(int stepsPerDirection) {
        this.stepsPerDirection = stepsPerDirection;
        this.currentIndex = 0;
        this.stepsTaken = 0;
    }

    public CircularMovementPattern() {
        this(3); // 3 pasos por dirección por defecto
    }

    @Override
    public Direction calculateNextDirection(Enemy enemy, GameMap map) {
        Direction direction = PATTERN[currentIndex];
        var nextPosition = enemy.getPosition().move(direction);

        // Si la posición está bloqueada, intentar la siguiente dirección en el patrón
        if (!map.isValidPosition(nextPosition) || map.isPositionBlocked(nextPosition)) {
            currentIndex = (currentIndex + 1) % PATTERN.length;
            stepsTaken = 0;
            return PATTERN[currentIndex];
        }

        return direction;
    }

    @Override
    public void update() {
        stepsTaken++;

        // Cambiar de dirección después de ciertos pasos
        if (stepsTaken >= stepsPerDirection) {
            currentIndex = (currentIndex + 1) % PATTERN.length;
            stepsTaken = 0;
        }
    }

    @Override
    public void reset() {
        this.currentIndex = 0;
        this.stepsTaken = 0;
    }
}
