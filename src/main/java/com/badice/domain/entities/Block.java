package com.badice.domain.entities;

import com.badice.domain.interfaces.Collidable;

/**
 * Representa un bloque sólido en el mapa (muro permanente).
 */
public class Block extends GameEntity implements Collidable {
    private static final String ENTITY_TYPE = "BLOCK";

    private String blockType; // wall, stone, etc.

    public Block(Position position, String blockType) {
        super(position);
        this.blockType = blockType;
    }

    public String getBlockType() {
        return blockType;
    }

    @Override
    public void accept(com.badice.domain.interfaces.EntityVisitor visitor) {
        visitor.visit(this);
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
        return true;
    }

    @Override
    public void onCollision(GameEntity other) {
        // Los bloques sólidos simplemente bloquean el movimiento
    }

    @Override
    protected void doUpdate() {
        // Los bloques son estáticos
    }

    @Override
    public String getEntityType() {
        return ENTITY_TYPE;
    }
}
