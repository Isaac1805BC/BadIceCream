package com.badice.domain.entities;

import com.badice.domain.interfaces.MovementPattern;

/**
 * Enemigo Calamar: Persigue al jugador y destruye bloques de hielo.
 */
public class SquidEnemy extends Enemy {

    public SquidEnemy(Position position, MovementPattern movementPattern) {
        super(position, movementPattern, "squid");
        setSpeed(1); // Velocidad normal
    }
}
