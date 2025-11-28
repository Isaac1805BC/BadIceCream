package com.badice.domain.entities;

import com.badice.domain.interfaces.MovementPattern;

/**
 * Implementación concreta de un enemigo básico.
 */
public class BasicEnemy extends Enemy {

    public BasicEnemy(Position position, MovementPattern movementPattern) {
        super(position, movementPattern, "basic");
    }
}
