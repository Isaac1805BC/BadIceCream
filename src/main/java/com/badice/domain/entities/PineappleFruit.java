package com.badice.domain.entities;

/**
 * Piña: Fruta que se mueve cuando el jugador se mueve, siguiendo la misma
 * dirección.
 * Otorga 200 puntos.
 */
public class PineappleFruit extends Fruit {
    private static final int POINTS = 200;

    private final Player player;
    private final GameMap gameMap;
    private Position lastPlayerPosition;
    private int moveCounter = 0;

    public PineappleFruit(Position position, Player player, GameMap gameMap) {
        super(position, "piña", POINTS);
        this.player = player;
        this.gameMap = gameMap;
        if (player != null) {
            this.lastPlayerPosition = player.getPosition();
        } else {
             // Fallback to fruit position if player is null (shouldn't happen in normal gameplay)
             this.lastPlayerPosition = position;
        }
    }

    @Override
    protected void doUpdate() {
        if (player == null || !player.isActive()) {
            return;
        }

        Position currentPlayerPosition = player.getPosition();

        // Verificar si el jugador se movió
        if (!currentPlayerPosition.equals(lastPlayerPosition)) {
            moveCounter++;
            // Moverse solo cada 2 movimientos del jugador para que sea posible alcanzarla
            if (moveCounter % 2 != 0) {
                // Calcular la dirección en la que se movió el jugador
                Direction playerDirection = calculateDirection(lastPlayerPosition, currentPlayerPosition);

                if (playerDirection != null) {
                    // Intentar mover la piña en la misma dirección
                    tryMove(playerDirection);
                }
            }

            // Actualizar la última posición conocida del jugador
            lastPlayerPosition = currentPlayerPosition;
        }
    }

    /**
     * Calcula la dirección del movimiento entre dos posiciones.
     */
    private Direction calculateDirection(Position from, Position to) {
        int dx = to.getX() - from.getX();
        int dy = to.getY() - from.getY();

        if (dx > 0)
            return Direction.RIGHT;
        if (dx < 0)
            return Direction.LEFT;
        if (dy > 0)
            return Direction.DOWN;
        if (dy < 0)
            return Direction.UP;

        return null;
    }

    /**
     * Intenta mover la piña en la dirección especificada.
     */
    private void tryMove(Direction direction) {
        Position newPosition = position.move(direction);

        // Verificar que la nueva posición sea válida
        if (!gameMap.isValidPosition(newPosition)) {
            return;
        }

        // Verificar si está bloqueada, PERO ignorar al jugador (para permitir ser
        // recogida)
        boolean blockedBySolid = gameMap.getEntities().stream()
                .filter(e -> e.isActive() && e instanceof com.badice.domain.interfaces.Collidable)
                .map(e -> (com.badice.domain.interfaces.Collidable) e)
                .filter(com.badice.domain.interfaces.Collidable::isSolid)
                .filter(e -> !e.equals(player)) // Ignorar al jugador
                .anyMatch(e -> e.getCollisionPosition().equals(newPosition));

        if (!blockedBySolid) {
            this.position = newPosition;
        }
    }

    @Override
    public void accept(com.badice.domain.interfaces.EntityVisitor visitor) {
        visitor.visit(this);
    }
}
