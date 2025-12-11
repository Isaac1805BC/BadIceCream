package com.badice.domain.entities;

import com.badice.domain.patterns.ChaseMovementPattern;

/**
 * Enemigo Maceta: Se mueve rápido persiguiendo al jugador.
 */
public class PotEnemy extends Enemy {

    public PotEnemy(Position position) {
        super(position, new ChaseMovementPattern(), "pot");
    }

    @Override
    public void accept(com.badice.domain.interfaces.EntityVisitor visitor) {
        visitor.visit(this);
    }

    /**
     * La maceta no puede romper bloques de hielo.
     * Este método se puede usar para identificar si este enemigo puede destruir
     * paredes de hielo.
     */
    public boolean canBreakIce() {
        return false;
    }
}
