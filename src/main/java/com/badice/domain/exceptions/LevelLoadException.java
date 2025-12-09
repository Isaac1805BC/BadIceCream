package com.badice.domain.exceptions;

/**
 * Excepción lanzada cuando hay un error al cargar un nivel.
 */
public class LevelLoadException extends GameException {
    private static final long serialVersionUID = 1L;

    private final int levelNumber;
    private final String fileName;

    public LevelLoadException(String message, int levelNumber) {
        super(message, "LEVEL_LOAD_ERROR");
        this.levelNumber = levelNumber;
        this.fileName = null;
    }

    public LevelLoadException(String message, String fileName) {
        super(message, "LEVEL_LOAD_ERROR");
        this.levelNumber = -1;
        this.fileName = fileName;
    }

    public LevelLoadException(String message, int levelNumber, Throwable cause) {
        super(message, "LEVEL_LOAD_ERROR", cause);
        this.levelNumber = levelNumber;
        this.fileName = null;
    }

    public LevelLoadException(String message, String fileName, Throwable cause) {
        super(message, "LEVEL_LOAD_ERROR", cause);
        this.levelNumber = -1;
        this.fileName = fileName;
    }

    public int getLevelNumber() {
        return levelNumber;
    }

    public String getFileName() {
        return fileName;
    }

    @Override
    public String toString() {
        if (fileName != null) {
            return String.format("[LEVEL_LOAD_ERROR] Failed to load level from file '%s': %s",
                    fileName, getMessage());
        } else if (levelNumber > 0) {
            return String.format("[LEVEL_LOAD_ERROR] Failed to load level %d: %s",
                    levelNumber, getMessage());
        }
        return super.toString();
    }
}
