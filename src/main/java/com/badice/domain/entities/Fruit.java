package com.badice.domain.entities;

import com.badice.domain.interfaces.Collidable;

/**
 * Clase abstracta que representa una fruta coleccionable en el juego.
 * Las subclases definen comportamientos específicos de cada tipo de fruta.
 */
public abstract class Fruit extends GameEntity implements Collidable {
    private static final String ENTITY_TYPE = "FRUIT";

    protected String fruitType;
    protected int points;
    protected boolean collected;

    public Fruit(Position position, String fruitType, int points) {
        super(position);
        this.fruitType = fruitType;
        this.points = points;
        this.collected = false;
    }

    public String getFruitType() {
        return fruitType;
    }

    public int getPoints() {
        return points;
    }

    public boolean isCollected() {
        return collected;
    }

    public void collect() {
        this.collected = true;
        this.active = false;
    }

    @Override
    public boolean collidesWith(GameEntity other) {
        return !collected && this.position.equals(other.getPosition());
    }

    @Override
    public Position getCollisionPosition() {
        return this.position;
    }

    @Override
    public boolean isSolid() {
        return false; // Las frutas no bloquean el movimiento
    }

    @Override
    public void onCollision(GameEntity other) {
        if (other instanceof Player && !collected) {
            collect();
            ((Player) other).addScore(points);
        }
    }

    @Override
    public String getEntityType() {
        return ENTITY_TYPE;
    }

    /**
     * Método abstracto para definir el comportamiento específico de cada tipo de
     * fruta.
     * Las subclases deben implementar su lógica de actualización personalizada.
     */
    @Override
    protected abstract void doUpdate();
}
