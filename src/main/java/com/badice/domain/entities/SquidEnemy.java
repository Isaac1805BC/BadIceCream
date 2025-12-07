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

    /**
     * Verifica si este enemigo puede romper hielos.
     */
    public boolean canBreakIce() {
        return true;
    }

    /**
     * Intenta romper un bloque de hielo en la posición dada.
     */
    public void breakIceAt(Position position, GameMap map) {
        map.getEntities().stream()
                .filter(entity -> entity instanceof IceBlock)
                .filter(entity -> entity.getPosition().equals(position))
                .filter(entity -> entity.isActive())
                .forEach(entity -> {
                    IceBlock ice = (IceBlock) entity;
                    ice.destroy();
                    System.out.println("¡Calamar destruyó hielo en " + position + "!");
                });
    }
}