package com.badice.domain.entities;

/**
 * Cereza: Fruta que cambia de posición aleatoriamente.
 * Otorga 150 puntos.
 */
public class CherryFruit extends Fruit {
    private static final long TELEPORT_INTERVAL = 5000; // 5 segundos
    private long lastTeleportTime;
    private final GameMap gameMap;

    public CherryFruit(Position position, GameMap gameMap) {
        super(position, "cereza", 150);
        this.gameMap = gameMap;
        this.lastTeleportTime = System.currentTimeMillis();
    }

    @Override
    protected void doUpdate() {
        long currentTime = System.currentTimeMillis();

        if (currentTime - lastTeleportTime >= TELEPORT_INTERVAL) {
            teleport();
            lastTeleportTime = currentTime;
        }
    }

    private void teleport() {
        // Encontrar una posición vacía aleatoria
        // Por simplicidad, intentaremos 10 veces encontrar una posición válida
        for (int i = 0; i < 10; i++) {
            int x = (int) (Math.random() * gameMap.getWidth());
            int y = (int) (Math.random() * gameMap.getHeight());
            Position newPos = new Position(x, y);

            if (isValidTeleportPosition(newPos)) {
                this.position = newPos;
                break;
            }
        }
    }

    private boolean isValidTeleportPosition(Position pos) {
        // Verificar límites
        if (!gameMap.isValidPosition(pos)) return false;

        // Verificar colisiones con otras entidades sólidas
        return gameMap.getEntities().stream()
                .filter(GameEntity::isActive)
                .noneMatch(e -> e.getPosition().equals(pos) && (e instanceof com.badice.domain.interfaces.Collidable && ((com.badice.domain.interfaces.Collidable) e).isSolid()));
    }

    @Override
    public void accept(com.badice.domain.interfaces.EntityVisitor visitor) {
        visitor.visit(this);
    }
}
