package com.badice.domain.entities;

/**
 * Cereza: Fruta que se teletransporta cada 20 segundos a una posición aleatoria
 * libre.
 * Otorga 150 puntos.
 */
public class CherryFruit extends Fruit {
    private static final int TELEPORT_INTERVAL_MS = 20000; // 20 segundos
    private static final int POINTS = 150;

    private final GameMap gameMap;
    private long lastTeleportTime;

    public CherryFruit(Position position, GameMap gameMap) {
        super(position, "cereza", POINTS);
        this.gameMap = gameMap;
        this.lastTeleportTime = System.currentTimeMillis();
    }

    @Override
    protected void doUpdate() {
        long currentTime = System.currentTimeMillis();

        // Verificar si han pasado 20 segundos desde el último teletransporte
        if (currentTime - lastTeleportTime >= TELEPORT_INTERVAL_MS) {
            teleportToRandomPosition();
            lastTeleportTime = currentTime;
        }
    }

    /**
     * Teletransporta la cereza a una posición aleatoria libre en el mapa.
     */
    private void teleportToRandomPosition() {
        Position newPosition = gameMap.findRandomFreePosition();
        if (newPosition != null) {
            this.position = newPosition;
        }
    }
}
