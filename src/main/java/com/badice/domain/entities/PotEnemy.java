package com.badice.domain.entities;

import com.badice.domain.patterns.ChaseMovementPattern;

/**
 * Maceta: Enemigo que persigue al jugador pero no puede romper bloques de
 * hielo.
 */
public class PotEnemy extends Enemy {

    public PotEnemy(Position position) {
        super(position, new ChaseMovementPattern(), "pot");
    }

    /**
     * La maceta no puede romper bloques de hielo.
     * Este método se puede usar para identificar si este enemigo puede destruir
     * hielo.
     */
    public boolean canBreakIce() {
        return false;
    }
}
