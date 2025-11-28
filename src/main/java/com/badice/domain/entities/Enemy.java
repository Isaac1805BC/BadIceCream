package com.badice.domain.entities;

import com.badice.domain.interfaces.Collidable;
import com.badice.domain.interfaces.Movable;
import com.badice.domain.interfaces.MovementPattern;

/**
 * Representa un enemigo en el juego.
 */
public abstract class Enemy extends GameEntity implements Movable, Collidable {
    private static final int DEFAULT_SPEED = 1;
    private static final String ENTITY_TYPE = "ENEMY";

    private int speed;
    private MovementPattern movementPattern;
    private Direction currentDirection;
    private String enemyType;

    protected Enemy(Position position, MovementPattern movementPattern, String enemyType) {
        super(position);
        this.speed = DEFAULT_SPEED;
        this.movementPattern = movementPattern;
        this.currentDirection = Direction.RIGHT;
        this.enemyType = enemyType;
    }

    @Override
    public void move(Direction direction) {
        this.currentDirection = direction;
        this.position = position.move(direction);
    }

    @Override
    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public MovementPattern getMovementPattern() {
        return movementPattern;
    }

    public void setMovementPattern(MovementPattern pattern) {
        this.movementPattern = pattern;
    }

    public Direction getCurrentDirection() {
        return currentDirection;
    }

    public String getEnemyType() {
        return enemyType;
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
        // La lógica de colisión específica se maneja en el CollisionDetector
    }

    @Override
    protected void doUpdate() {
        if (movementPattern != null) {
            movementPattern.update();
        }
    }

    @Override
    public String getEntityType() {
        return ENTITY_TYPE;
    }
}
