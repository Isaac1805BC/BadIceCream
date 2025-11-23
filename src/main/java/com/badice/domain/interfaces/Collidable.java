package com.badice.domain.interfaces;

import com.badice.domain.entities.GameEntity;
import com.badice.domain.entities.Position;

/**
 * Interfaz para entidades que pueden colisionar con otras.
 */
public interface Collidable {
    /**
     * Verifica si esta entidad colisiona con otra en una posición dada.
     */
    boolean collidesWith(GameEntity other);

    /**
     * Obtiene la posición de colisión de la entidad.
     */
    Position getCollisionPosition();

    /**
     * Indica si esta entidad es sólida (bloquea el movimiento).
     */
    boolean isSolid();

    /**
     * Maneja la colisión con otra entidad.
     */
    void onCollision(GameEntity other);
}
