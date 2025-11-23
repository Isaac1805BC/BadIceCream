package com.badice.domain.entities;

import com.badice.domain.interfaces.Collidable;

/**
 * Representa una fruta coleccionable en el juego.
 */
public class Fruit extends GameEntity implements Collidable {
    private static final String ENTITY_TYPE = "FRUIT";

    private String fruitType; // manzana, fresa, cereza, etc.
    private int points;
    private boolean collected;

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
    protected void doUpdate() {
        // Las frutas son estáticas, no necesitan actualización
    }

    @Override
    public String getEntityType() {
        return ENTITY_TYPE;
    }
}
