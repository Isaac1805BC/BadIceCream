package com.badice.domain.config;

import com.badice.domain.entities.Position;
import java.util.*;

/**
 * Configuración de un nivel cargado desde archivo externo.
 */
public class LevelConfig {
    private int mapWidth;
    private int mapHeight;
    private int totalPhases;
    private List<PhaseConfig> phases;

    public LevelConfig() {
        this.phases = new ArrayList<>();
    }

    public int getMapWidth() {
        return mapWidth;
    }

    public void setMapWidth(int mapWidth) {
        this.mapWidth = mapWidth;
    }

    public int getMapHeight() {
        return mapHeight;
    }

    public void setMapHeight(int mapHeight) {
        this.mapHeight = mapHeight;
    }

    public int getTotalPhases() {
        return totalPhases;
    }

    public void setTotalPhases(int totalPhases) {
        this.totalPhases = totalPhases;
    }

    public List<PhaseConfig> getPhases() {
        return phases;
    }

    public void addPhase(PhaseConfig phase) {
        this.phases.add(phase);
    }

    /**
     * Configuración de una fase dentro de un nivel.
     */
    public static class PhaseConfig {
        private int phaseNumber;
        private List<EntityConfig> entities;

        public PhaseConfig(int phaseNumber) {
            this.phaseNumber = phaseNumber;
            this.entities = new ArrayList<>();
        }

        public int getPhaseNumber() {
            return phaseNumber;
        }

        public List<EntityConfig> getEntities() {
            return entities;
        }

        public void addEntity(EntityConfig entity) {
            this.entities.add(entity);
        }
    }

    /**
     * Configuración de una entidad (fruta, enemigo, obstáculo).
     */
    public static class EntityConfig {
        private String type; // FRUIT, ENEMY, BLOCK, CAMPFIRE, HOTTILE
        private String subType; // BANANA, GRAPE, TROLL, etc.
        private Position position;
        private int points; // Solo para frutas

        public EntityConfig(String type, String subType, Position position) {
            this.type = type;
            this.subType = subType;
            this.position = position;
            this.points = 0;
        }

        public EntityConfig(String type, String subType, Position position, int points) {
            this.type = type;
            this.subType = subType;
            this.position = position;
            this.points = points;
        }

        public String getType() {
            return type;
        }

        public String getSubType() {
            return subType;
        }

        public Position getPosition() {
            return position;
        }

        public int getPoints() {
            return points;
        }
    }
}
