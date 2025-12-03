package com.badice.domain.entities;

import com.badice.domain.interfaces.Collidable;
import com.badice.domain.interfaces.Movable;

/**
 * Representa al jugador en el juego.
 */
public class Player extends GameEntity implements Movable, Collidable {
    private static final int DEFAULT_SPEED = 1;
    private static final String ENTITY_TYPE = "PLAYER";

    private int speed;
    private int lives;
    private int score;
    private Direction currentDirection;
    private String playerColor; // "red", "brown", "blue"

    public Player(Position position) {
        super(position);
        this.speed = DEFAULT_SPEED;
        this.lives = 1; // Solo 1 vida - muerte instantánea
        this.score = 0;
        this.currentDirection = Direction.RIGHT;
        this.playerColor = "blue"; // Color por defecto
    }

    public Player(Position position, String color) {
        super(position);
        this.speed = DEFAULT_SPEED;
        this.lives = 1;
        this.score = 0;
        this.currentDirection = Direction.RIGHT;
        this.playerColor = color;
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

    public int getLives() {
        return lives;
    }

    public void loseLife() {
        this.lives--;
        if (lives <= 0) {
            this.active = false;
        }
    }

    public void setInactive() {
        this.active = false;
    }

    public void addLife() {
        this.lives++;
    }

    public int getScore() {
        return score;
    }

    public void addScore(int points) {
        this.score += points;
    }

    public Direction getCurrentDirection() {
        return currentDirection;
    }

    public String getPlayerColor() {
        return playerColor;
    }

    public void setPlayerColor(String color) {
        this.playerColor = color;
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
        // Lógica de actualización del jugador si es necesaria
    }

    @Override
    public String getEntityType() {
        return ENTITY_TYPE;
    }
}
