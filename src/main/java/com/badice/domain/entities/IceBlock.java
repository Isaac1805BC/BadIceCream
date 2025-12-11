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
    private static final long CRACK_DELAY = 300; // 300ms entre estados de ruptura

    public enum IceState {
        INTACT,
        CRACKED,
        BROKEN
    }

    private int health;
    private IceState state;
    private long crackTime; // Momento en que se agrietó

    public IceBlock(Position position) {
        super(position);
        this.health = DEFAULT_HEALTH;
        this.state = IceState.INTACT;
        this.crackTime = 0;
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
        // Verificar transición de estados
        if (state == IceState.CRACKED && crackTime > 0) {
            long elapsed = System.currentTimeMillis() - crackTime;
            if (elapsed >= CRACK_DELAY) {
                state = IceState.BROKEN;
                crackTime = System.currentTimeMillis();
            }
        } else if (state == IceState.BROKEN && crackTime > 0) {
            long elapsed = System.currentTimeMillis() - crackTime;
            if (elapsed >= CRACK_DELAY) {
                destroy();
            }
        }
    }

    /**
     * Inicia el proceso de agrietamiento del hielo.
     */
    public void crack() {
        if (state == IceState.INTACT) {
            state = IceState.CRACKED;
            crackTime = System.currentTimeMillis();
        }
    }

    /**
     * Obtiene el estado actual del bloque de hielo.
     */
    public IceState getState() {
        return state;
    }

    @Override
    public void accept(com.badice.domain.interfaces.EntityVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String getEntityType() {
        return ENTITY_TYPE;
    }
}
