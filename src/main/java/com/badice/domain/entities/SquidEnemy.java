package com.badice.domain.entities;

import com.badice.domain.interfaces.MovementPattern;

/**
 * Enemigo Calamar: Persigue al jugador y destruye bloques de hielo.
 */
public class SquidEnemy extends Enemy {
    private boolean isBreakingIce;
    private long breakingIceStartTime;
    private static final long BREAKING_ICE_DURATION = 300; // 300ms de animación

    public SquidEnemy(Position position, MovementPattern movementPattern) {
        super(position, movementPattern, "squid");
        setSpeed(1); // Velocidad normal
        this.isBreakingIce = false;
        this.breakingIceStartTime = 0;
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
        // Activar animación de romper hielo
        this.isBreakingIce = true;
        this.breakingIceStartTime = System.currentTimeMillis();

        map.getEntities().stream()
                .filter(entity -> entity instanceof IceBlock)
                .filter(entity -> entity.getPosition().equals(position))
                .filter(entity -> entity.isActive())
                .forEach(entity -> {
                    IceBlock ice = (IceBlock) entity;
                    ice.destroy();
                });
    }

    @Override
    protected void doUpdate() {
        super.doUpdate();

        // Desactivar animación de romper hielo después del tiempo
        if (isBreakingIce && System.currentTimeMillis() - breakingIceStartTime >= BREAKING_ICE_DURATION) {
            isBreakingIce = false;
        }
    }

    /**
     * Verifica si el calamar está rompiendo hielo actualmente.
     */
    public boolean isBreakingIce() {
        return isBreakingIce;
    }

    @Override
    public void accept(com.badice.domain.interfaces.EntityVisitor visitor) {
        visitor.visit(this);
    }
}