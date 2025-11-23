package com.badice.domain.entities;

import com.badice.domain.interfaces.Collidable;
import com.badice.domain.interfaces.Destructible;

/**
 * Representa un bloque de hielo que puede ser creado y destruido por el
 * jugador.
 */
public class IceBlock extends GameEntity implements Collidable, Destructible {
    private static final String ENTITY_TYPE = "ICE_BLOCK";
    private static final int DEFAULT_HEALTH = 1;

    private int health;

    public IceBlock(Position position) {
        super(position);
        this.health = DEFAULT_HEALTH;
    }

    @Override
    public void destroy() {
        this.health = 0;
        this.active = false;
    }

    @Override
    public boolean isDestroyed() {
        return !active || health <= 0;
    }

    @Override
    public int getHealth() {
        return health;
    }

    @Override
    public void takeDamage(int damage) {
        this.health -= damage;
        if (health <= 0) {
            destroy();
        }
    }

    @Override
    public boolean collidesWith(GameEntity other) {
        return this.position.equals(other.getPosition());
    }

    @Override
    public Position getCollisionPosition() {
        return this.position;
    }

    @Override
    public boolean isSolid() {
        return active && !isDestroyed();
    }

    @Override
    public void onCollision(GameEntity other) {
        // El hielo bloquea el movimiento pero puede ser destruido
    }

    @Override
    protected void doUpdate() {
        // Los bloques de hielo son estáticos
    }

    @Override
    public String getEntityType() {
        return ENTITY_TYPE;
    }
}
