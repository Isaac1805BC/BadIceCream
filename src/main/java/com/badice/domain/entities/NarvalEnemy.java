package com.badice.domain.entities;

import com.badice.domain.interfaces.MovementPattern;

/**
 * Enemigo Narval: Embiste en línea recta cuando ve al jugador.
 * Rompe bloques de hielo durante la embestida.
 */
public class NarvalEnemy extends Enemy {
    private boolean isCharging;
    private Direction chargeDirection;
    private long lastChargeTime;
    private static final int CHARGE_SPEED = 3; // Velocidad durante embestida

    public NarvalEnemy(Position position, MovementPattern movementPattern) {
        super(position, movementPattern, "narval");
        this.isCharging = false;
        this.chargeDirection = null;
        this.lastChargeTime = 0;
    }

    /**
     * Verifica si el narval está alineado con el jugador (misma fila o columna)
     */
    public boolean isAlignedWithPlayer(Player player) {
        if (isCharging) return false; // Ya está cargando

        int dx = Math.abs(player.getPosition().getX() - this.position.getX());
        int dy = Math.abs(player.getPosition().getY() - this.position.getY());

        // Alineado si está en la misma fila (dy=0) o columna (dx=0)
        return dx == 0 || dy == 0;
    }

    /**
     * Inicia la embestida hacia la dirección actual o calculada.
     */
    public void startCharge() {
        if (isCharging) return;

        this.isCharging = true;
        this.chargeDirection = this.getCurrentDirection(); // Cargar en la dirección que mira
        // Podríamos recalcular la dirección hacia el jugador aquí si fuera necesario
    }

    /**
     * Detiene la embestida.
     */
    public void stopCharge() {
        this.isCharging = false;
        this.chargeDirection = null;
        this.lastChargeTime = System.currentTimeMillis();
    }

    public boolean isCharging() {
        return isCharging;
    }

    public Direction getChargeDirection() {
        return chargeDirection;
    }

    @Override
    protected void doUpdate() {
        super.doUpdate();
        // Lógica adicional de actualización si es necesaria
        // Por ejemplo, detener carga si choca con pared indestructible (gestionado en engine)
    }

    @Override
    public void accept(com.badice.domain.interfaces.EntityVisitor visitor) {
        visitor.visit(this);
    }
}
