package com.badice.domain.entities;

import com.badice.domain.interfaces.MovementPattern;

/**
 * Enemigo tipo Troll - se mueve de izquierda a derecha.
 * Este es el enemigo básico del nivel 1.
 */
public class TrollEnemy extends Enemy {

    public TrollEnemy(Position position, MovementPattern movementPattern) {
        super(position, movementPattern, "troll");
    }
}
