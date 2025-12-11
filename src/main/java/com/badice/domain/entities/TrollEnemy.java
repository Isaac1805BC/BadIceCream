package com.badice.domain.entities;

import com.badice.domain.interfaces.MovementPattern;

/**
 * Enemigo Troll: Movimiento lento y errático.
 */
public class TrollEnemy extends Enemy {

    public TrollEnemy(Position position, MovementPattern movementPattern) {
        super(position, movementPattern, "troll");
    }

    @Override
    public void accept(com.badice.domain.interfaces.EntityVisitor visitor) {
        visitor.visit(this);
    }
}
