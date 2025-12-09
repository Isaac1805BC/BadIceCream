package com.badice.domain.services;

import com.badice.domain.config.LevelConfig;
import com.badice.domain.config.LevelConfig.PhaseConfig;
import com.badice.domain.config.LevelConfig.EntityConfig;
import com.badice.domain.entities.Position;
import com.badice.domain.exceptions.LevelLoadException;
import com.badice.domain.exceptions.InvalidConfigurationException;
import com.badice.domain.utils.GameLogger;

import java.io.*;

/**
 * Servicio para cargar niveles desde archivos de configuración .txt
 */
public class LevelLoader {
    private static final String LEVELS_DIR = "levels/";
    private static final GameLogger logger = GameLogger.getInstance();

    /**
     * Carga un nivel desde un archivo .txt
     */
    public LevelConfig loadLevel(int levelNumber) throws LevelLoadException {
        String fileName = LEVELS_DIR + "level" + levelNumber + ".txt";

        try {
            return loadLevelFromFile(fileName);
        } catch (IOException e) {
            logger.logError("Failed to load level " + levelNumber, e);
            throw new LevelLoadException("No se pudo cargar el nivel " + levelNumber, fileName, e);
        } catch (InvalidConfigurationException e) {
            logger.logGameException(e);
            throw new LevelLoadException("Configuración inválida en nivel " + levelNumber, fileName, e);
        }
    }

    /**
     * Carga un nivel desde un archivo específico.
     */
    private LevelConfig loadLevelFromFile(String fileName) throws IOException, InvalidConfigurationException {
        LevelConfig config = new LevelConfig();
        PhaseConfig currentPhase = null;

        File file = new File(fileName);
        if (!file.exists()) {
            throw new FileNotFoundException("Archivo de nivel no encontrado: " + fileName);
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            int lineNumber = 0;

            while ((line = reader.readLine()) != null) {
                lineNumber++;
                line = line.trim();

                // Ignorar líneas vacías y comentarios
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }

                try {
                    parseLine(line, config, currentPhase);

                    // Actualizar referencia a fase actual si se creó una nueva
                    if (line.startsWith("PHASE=") && !config.getPhases().isEmpty()) {
                        currentPhase = config.getPhases().get(config.getPhases().size() - 1);
                    }
                } catch (Exception e) {
                    throw new InvalidConfigurationException(
                            "Error en línea " + lineNumber + ": " + line,
                            "LINE_" + lineNumber,
                            line);
                }
            }
        }

        // Validar configuración
        validateConfig(config, fileName);

        logger.logInfo("Nivel cargado exitosamente: " + fileName);
        return config;
    }

    /**
     * Parsea una línea del archivo de configuración.
     */
    private void parseLine(String line, LevelConfig config, PhaseConfig currentPhase)
            throws InvalidConfigurationException {

        if (line.startsWith("MAP_WIDTH=")) {
            int width = Integer.parseInt(line.substring("MAP_WIDTH=".length()));
            config.setMapWidth(width);
        } else if (line.startsWith("MAP_HEIGHT=")) {
            int height = Integer.parseInt(line.substring("MAP_HEIGHT=".length()));
            config.setMapHeight(height);
        } else if (line.startsWith("PHASES=")) {
            int phases = Integer.parseInt(line.substring("PHASES=".length()));
            config.setTotalPhases(phases);
        } else if (line.startsWith("PHASE=")) {
            int phaseNum = Integer.parseInt(line.substring("PHASE=".length()));
            PhaseConfig phase = new PhaseConfig(phaseNum);
            config.addPhase(phase);
        } else if (line.startsWith("FRUIT=")) {
            if (currentPhase == null) {
                throw new InvalidConfigurationException("FRUIT definido fuera de una PHASE", "FRUIT", line);
            }
            parseFruit(line, currentPhase);
        } else if (line.startsWith("ENEMY=")) {
            if (currentPhase == null) {
                throw new InvalidConfigurationException("ENEMY definido fuera de una PHASE", "ENEMY", line);
            }
            parseEnemy(line, currentPhase);
        } else if (line.startsWith("BLOCK=")) {
            if (currentPhase == null) {
                throw new InvalidConfigurationException("BLOCK definido fuera de una PHASE", "BLOCK", line);
            }
            parseBlock(line, currentPhase);
        } else if (line.startsWith("CAMPFIRE=")) {
            if (currentPhase == null) {
                throw new InvalidConfigurationException("CAMPFIRE definido fuera de una PHASE", "CAMPFIRE", line);
            }
            parseCampfire(line, currentPhase);
        } else if (line.startsWith("HOTTILE=")) {
            if (currentPhase == null) {
                throw new InvalidConfigurationException("HOTTILE definido fuera de una PHASE", "HOTTILE", line);
            }
            parseHotTile(line, currentPhase);
        }
    }

    private void parseFruit(String line, PhaseConfig phase) {
        // Formato: FRUIT=TYPE,X,Y,POINTS
        String data = line.substring("FRUIT=".length());
        String[] parts = data.split(",");

        String fruitType = parts[0].trim();
        int x = Integer.parseInt(parts[1].trim());
        int y = Integer.parseInt(parts[2].trim());
        int points = Integer.parseInt(parts[3].trim());

        EntityConfig entity = new EntityConfig("FRUIT", fruitType, new Position(x, y), points);
        phase.addEntity(entity);
    }

    private void parseEnemy(String line, PhaseConfig phase) {
        // Formato: ENEMY=TYPE,X,Y
        String data = line.substring("ENEMY=".length());
        String[] parts = data.split(",");

        String enemyType = parts[0].trim();
        int x = Integer.parseInt(parts[1].trim());
        int y = Integer.parseInt(parts[2].trim());

        EntityConfig entity = new EntityConfig("ENEMY", enemyType, new Position(x, y));
        phase.addEntity(entity);
    }

    private void parseBlock(String line, PhaseConfig phase) {
        // Formato: BLOCK=X,Y
        String data = line.substring("BLOCK=".length());
        String[] parts = data.split(",");

        int x = Integer.parseInt(parts[0].trim());
        int y = Integer.parseInt(parts[1].trim());

        EntityConfig entity = new EntityConfig("BLOCK", "ICE", new Position(x, y));
        phase.addEntity(entity);
    }

    private void parseCampfire(String line, PhaseConfig phase) {
        // Formato: CAMPFIRE=X,Y
        String data = line.substring("CAMPFIRE=".length());
        String[] parts = data.split(",");

        int x = Integer.parseInt(parts[0].trim());
        int y = Integer.parseInt(parts[1].trim());

        EntityConfig entity = new EntityConfig("CAMPFIRE", "FIRE", new Position(x, y));
        phase.addEntity(entity);
    }

    private void parseHotTile(String line, PhaseConfig phase) {
        // Formato: HOTTILE=X,Y
        String data = line.substring("HOTTILE=".length());
        String[] parts = data.split(",");

        int x = Integer.parseInt(parts[0].trim());
        int y = Integer.parseInt(parts[1].trim());

        EntityConfig entity = new EntityConfig("HOTTILE", "HOT", new Position(x, y));
        phase.addEntity(entity);
    }

    /**
     * Valida que la configuración sea correcta.
     */
    private void validateConfig(LevelConfig config, String fileName) throws InvalidConfigurationException {
        if (config.getMapWidth() <= 0 || config.getMapHeight() <= 0) {
            throw new InvalidConfigurationException(
                    "Dimensiones de mapa inválidas en " + fileName,
                    "MAP_SIZE",
                    config.getMapWidth() + "x" + config.getMapHeight());
        }

        if (config.getTotalPhases() <= 0) {
            throw new InvalidConfigurationException(
                    "Número de fases inválido en " + fileName,
                    "PHASES",
                    String.valueOf(config.getTotalPhases()));
        }

        if (config.getPhases().isEmpty()) {
            throw new InvalidConfigurationException(
                    "No se definieron fases en " + fileName,
                    "PHASES",
                    "0");
        }
    }
}
