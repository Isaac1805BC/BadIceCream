package com.badice.domain.exceptions;

/**
 * Clase base para todas las excepciones personalizadas del juego.
 * Proporciona funcionalidad común para el manejo de errores.
 */
public class GameException extends Exception {
    private static final long serialVersionUID = 1L;

    private final String errorCode;
    private final long timestamp;

    public GameException(String message) {
        super(message);
        this.errorCode = "GAME_ERROR";
        this.timestamp = System.currentTimeMillis();
    }

    public GameException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
        this.timestamp = System.currentTimeMillis();
    }

    public GameException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "GAME_ERROR";
        this.timestamp = System.currentTimeMillis();
    }

    public GameException(String message, String errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.timestamp = System.currentTimeMillis();
    }

    public String getErrorCode() {
        return errorCode;
    }

    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s - %s (timestamp: %d)",
                errorCode, getClass().getSimpleName(), getMessage(), timestamp);
    }
}
