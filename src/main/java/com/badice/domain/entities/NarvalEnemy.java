package com.badice.domain.entities;

import com.badice.domain.interfaces.MovementPattern;

/**
 * Enemigo Narval: Recorre el mapa sin perseguir al jugador.
 * Cuando se alinea horizontal o verticalmente con el jugador, embiste en esa
 * dirección.
 * Durante la embestida, destruye bloques de hielo.
 */
public class NarvalEnemy extends Enemy {
    private static final int CHARGE_SPEED = 2; // Velocidad de embestida
    private static final int NORMAL_SPEED = 1; // Velocidad normal

    private boolean isCharging; // true si está embistiendo
    private Direction chargeDirection; // Dirección de la embestida
    private int chargeCounter; // Contador de frames de embestida
    private static final int CHARGE_DURATION = 10; // Duración de embestida en frames

    public NarvalEnemy(Position position, MovementPattern movementPattern) {
        super(position, movementPattern, "narval");
        this.isCharging = false;
        this.chargeDirection = null;
        this.chargeCounter = 0;
        setSpeed(NORMAL_SPEED);
    }

    /**
     * Actualiza el estado del Narval.
     * Verifica alineación con jugador y realiza embestida si corresponde.
     */
    @Override
    protected void doUpdate() {
        if (isCharging) {
            // Continuar embestida
            chargeCounter++;
            if (chargeCounter >= CHARGE_DURATION) {
                // Terminar embestida
                endCharge();
            }
        } else {
            // Actualización normal del patrón de movimiento
            super.doUpdate();
        }
    }

    /**
     * Verifica si el Narval está alineado horizontal o verticalmente con el player.
     */
    public boolean isAlignedWithPlayer(Player player) {
        if (player == null || !player.isActive()) {
            return false;
        }

        Position playerPos = player.getPosition();
        // Alineados horizontalmente (misma Y)
        if (this.position.getY() == playerPos.getY()) {
            chargeDirection = (this.position.getX() < playerPos.getX()) ? Direction.RIGHT : Direction.LEFT;
            return true;
        }
        // Alineados verticalmente (misma X)
        if (this.position.getX() == playerPos.getX()) {
            chargeDirection = (this.position.getY() < playerPos.getY()) ? Direction.DOWN : Direction.UP;
            return true;
        }

        return false;
    }

    /**
     * Inicia la embestida en la dirección del jugador.
     */
    public void startCharge() {
        if (!isCharging && chargeDirection != null) {
            this.isCharging = true;
            this.chargeCounter = 0;
            setSpeed(CHARGE_SPEED);
            System.out.println("Narval charging in direction: " + chargeDirection);
        }
    }

    /**
     * Termina la embestida y vuelve al movimiento normal.
     */
    private void endCharge() {
        this.isCharging = false;
        this.chargeDirection = null;
        this.chargeCounter = 0;
        setSpeed(NORMAL_SPEED);
        System.out.println("Narval stopped charging");
    }

    /**
     * Obtiene la dirección de la embestida actual.
     */
    public Direction getChargeDirection() {
        return chargeDirection;
    }

    /**
     * Verifica si el Narval está embistiendo.
     */
    public boolean isCharging() {
        return isCharging;
    }

    @Override
    public void move(Direction direction) {
        // Si está embistiendo, solo se mueve en la dirección de carga
        if (isCharging) {
            super.move(chargeDirection);
        } else {
            super.move(direction);
        }
    }
}
