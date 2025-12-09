package com.badice.domain.exceptions;

import com.badice.domain.entities.Position;
import com.badice.domain.entities.Direction;

/**
 * Excepción lanzada cuando se intenta realizar un movimiento inválido.
 */
public class InvalidMoveException extends GameException {
    private static final long serialVersionUID = 1L;

    private final Position attemptedPosition;
    private final Direction attemptedDirection;

    public InvalidMoveException(String message) {
        super(message, "INVALID_MOVE");
        this.attemptedPosition = null;
        this.attemptedDirection = null;
    }

    public InvalidMoveException(String message, Position position, Direction direction) {
        super(message, "INVALID_MOVE");
        this.attemptedPosition = position;
        this.attemptedDirection = direction;
    }

    public InvalidMoveException(String message, Throwable cause) {
        super(message, "INVALID_MOVE", cause);
        this.attemptedPosition = null;
        this.attemptedDirection = null;
    }

    public Position getAttemptedPosition() {
        return attemptedPosition;
    }

    public Direction getAttemptedDirection() {
        return attemptedDirection;
    }

    @Override
    public String toString() {
        if (attemptedPosition != null && attemptedDirection != null) {
            return String.format("[INVALID_MOVE] Attempted to move to %s in direction %s: %s",
                    attemptedPosition, attemptedDirection, getMessage());
        }
        return super.toString();
    }
}
