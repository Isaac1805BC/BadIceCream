package com.badice.domain.exceptions;

/**
 * Excepción lanzada cuando hay un error al guardar o cargar el estado del
 * juego.
 */
public class PersistenceException extends GameException {
    private static final long serialVersionUID = 1L;

    private final String filePath;
    private final OperationType operationType;

    public enum OperationType {
        SAVE, LOAD
    }

    public PersistenceException(String message, String filePath, OperationType operationType) {
        super(message, "PERSISTENCE_ERROR");
        this.filePath = filePath;
        this.operationType = operationType;
    }

    public PersistenceException(String message, String filePath, OperationType operationType, Throwable cause) {
        super(message, "PERSISTENCE_ERROR", cause);
        this.filePath = filePath;
        this.operationType = operationType;
    }

    public String getFilePath() {
        return filePath;
    }

    public OperationType getOperationType() {
        return operationType;
    }

    @Override
    public String toString() {
        return String.format("[PERSISTENCE_ERROR] Failed to %s game state at '%s': %s",
                operationType.toString().toLowerCase(), filePath, getMessage());
    }
}
