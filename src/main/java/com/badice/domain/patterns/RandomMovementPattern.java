package com.badice.domain.patterns;

import com.badice.domain.entities.Direction;
import com.badice.domain.entities.Enemy;
import com.badice.domain.entities.GameMap;
import com.badice.domain.interfaces.MovementPattern;

import java.util.Random;

/**
 * Patrón de movimiento aleatorio: el enemigo cambia de dirección
 * aleatoriamente.
 */
public class RandomMovementPattern implements MovementPattern {
    private static final Direction[] DIRECTIONS = Direction.values();
    private final Random random;
    private Direction currentDirection;
    private int stepsTaken;
    private final int maxStepsBeforeChange;

    public RandomMovementPattern(int maxStepsBeforeChange) {
        this.random = new Random();
        this.maxStepsBeforeChange = maxStepsBeforeChange;
        this.currentDirection = DIRECTIONS[random.nextInt(DIRECTIONS.length)];
        this.stepsTaken = 0;
    }

    public RandomMovementPattern() {
        this(5); // Cambiar dirección cada 5 pasos por defecto
    }

    @Override
    public Direction calculateNextDirection(Enemy enemy, GameMap map) {
        var nextPosition = enemy.getPosition().move(currentDirection);

        // Si la posición está bloqueada, elegir una nueva dirección aleatoria
        if (!map.isValidPosition(nextPosition) || map.isPositionBlocked(nextPosition)) {
            chooseNewDirection(enemy, map);
        }

        return currentDirection;
    }

    private void chooseNewDirection(Enemy enemy, GameMap map) {
        // Intentar hasta 10 veces encontrar una dirección válida
        for (int attempt = 0; attempt < 10; attempt++) {
            Direction newDirection = DIRECTIONS[random.nextInt(DIRECTIONS.length)];
            var testPosition = enemy.getPosition().move(newDirection);

            if (map.isValidPosition(testPosition) && !map.isPositionBlocked(testPosition)) {
                currentDirection = newDirection;
                stepsTaken = 0;
                return;
            }
        }

        // Si no se encuentra dirección válida, quedarse quieto (no cambiar dirección)
    }

    @Override
    public void update() {
        stepsTaken++;

        // Cambiar de dirección aleatoriamente después de ciertos pasos
        if (stepsTaken >= maxStepsBeforeChange && random.nextDouble() < 0.3) {
            currentDirection = DIRECTIONS[random.nextInt(DIRECTIONS.length)];
            stepsTaken = 0;
        }
    }

    @Override
    public void reset() {
        this.currentDirection = DIRECTIONS[random.nextInt(DIRECTIONS.length)];
        this.stepsTaken = 0;
    }
}
