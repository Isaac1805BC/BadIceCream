package com.badice.domain.utils;

import com.badice.domain.exceptions.GameException;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Sistema de logging centralizado para el juego.
 * Registra errores y eventos importantes en un archivo de log.
 */
public class GameLogger {
    private static GameLogger instance;
    private static final String LOG_FILE = "game_error.log";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private PrintWriter logWriter;
    private boolean loggingEnabled = true;

    private GameLogger() {
        try {
            // Crear archivo de log en modo append
            logWriter = new PrintWriter(new FileWriter(LOG_FILE, true), true);
            logInfo("GameLogger initialized");
        } catch (IOException e) {
            System.err.println("Failed to initialize GameLogger: " + e.getMessage());
            loggingEnabled = false;
        }
    }

    public static synchronized GameLogger getInstance() {
        if (instance == null) {
            instance = new GameLogger();
        }
        return instance;
    }

    /**
     * Registra un error genérico.
     */
    public void logError(String message) {
        log("ERROR", message, null);
    }

    /**
     * Registra un error con excepción.
     */
    public void logError(String message, Throwable throwable) {
        log("ERROR", message, throwable);
    }

    /**
     * Registra una excepción del juego.
     */
    public void logGameException(GameException exception) {
        log("GAME_ERROR", exception.toString(), exception);
    }

    /**
     * Registra un warning.
     */
    public void logWarning(String message) {
        log("WARNING", message, null);
    }

    /**
     * Registra información general.
     */
    public void logInfo(String message) {
        log("INFO", message, null);
    }

    /**
     * Registra un mensaje de debug.
     */
    public void logDebug(String message) {
        log("DEBUG", message, null);
    }

    /**
     * Método principal de logging.
     */
    private synchronized void log(String level, String message, Throwable throwable) {
        if (!loggingEnabled || logWriter == null) {
            return;
        }

        String timestamp = DATE_FORMAT.format(new Date());
        String logMessage = String.format("[%s] [%s] %s", timestamp, level, message);

        // Escribir en archivo
        logWriter.println(logMessage);

        // Si hay excepción, escribir stack trace
        if (throwable != null) {
            throwable.printStackTrace(logWriter);
        }

        // También imprimir en consola para errores críticos
        if (level.equals("ERROR") || level.equals("GAME_ERROR")) {
            System.err.println(logMessage);
            if (throwable != null) {
                throwable.printStackTrace();
            }
        }
    }

    /**
     * Cierra el logger y libera recursos.
     */
    public void close() {
        if (logWriter != null) {
            logInfo("GameLogger shutting down");
            logWriter.close();
            logWriter = null;
        }
    }

    /**
     * Limpia el archivo de log.
     */
    public void clearLog() {
        try {
            if (logWriter != null) {
                logWriter.close();
            }
            new PrintWriter(LOG_FILE).close(); // Limpiar archivo
            logWriter = new PrintWriter(new FileWriter(LOG_FILE, true), true);
            logInfo("Log file cleared");
        } catch (IOException e) {
            System.err.println("Failed to clear log file: " + e.getMessage());
        }
    }
}
