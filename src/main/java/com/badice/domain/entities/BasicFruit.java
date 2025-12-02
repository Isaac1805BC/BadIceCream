package com.badice.domain.entities;

/**
 * Fruta básica estática que no tiene comportamiento especial.
 * Incluye frutas como banana, uva, cactus, etc.
 */
public class BasicFruit extends Fruit {

    public BasicFruit(Position position, String fruitType, int points) {
        super(position, fruitType, points);
    }

    @Override
    protected void doUpdate() {
        // Las frutas básicas son estáticas, no necesitan actualización
    }
}
