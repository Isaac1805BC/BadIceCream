package com.badice.domain.services;

import com.badice.domain.entities.Direction;
import com.badice.domain.entities.GameMap;
import com.badice.domain.entities.Position;
import com.badice.domain.interfaces.Movable;

/**
 * Servicio que valida y ejecuta movimientos de entidades.
 */
public class MovementService {
    private final CollisionDetector collisionDetector;

    public MovementService(CollisionDetector collisionDetector) {
        this.collisionDetector = collisionDetector;
    }

    /**
     * Mueve una entidad en la dirección especificada si es válido.
     * 
     * @return true si el movimiento fue exitoso, false si fue bloqueado
     */
    public boolean moveEntity(Movable entity, Direction direction, GameMap map) {
        Position currentPosition = entity.getPosition();
        Position targetPosition = currentPosition.move(direction);

        // Validar que la posición esté dentro del mapa
        if (!map.isValidPosition(targetPosition)) {
            return false;
        }

        // Verificar colisiones
        if (collisionDetector.willCollideWithSolid(targetPosition, map)) {
            return false;
        }

        // Ejecutar el movimiento
        entity.move(direction);
        return true;
    }

    /**
     * Intenta mover una entidad, devolviendo la posición resultante.
     */
    public Position calculateMovement(Position current, Direction direction, GameMap map) {
        Position target = current.move(direction);

        if (!map.isValidPosition(target) || map.isPositionBlocked(target)) {
            return current; // Mantener posición actual
        }

        return target;
    }

    /**
     * Verifica si una entidad puede moverse en una dirección.
     */
    public boolean canMove(Movable entity, Direction direction, GameMap map) {
        Position targetPosition = entity.getPosition().move(direction);

        return map.isValidPosition(targetPosition) &&
                !collisionDetector.willCollideWithSolid(targetPosition, map);
    }
}
