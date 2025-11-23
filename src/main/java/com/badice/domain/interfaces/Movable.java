package com.badice.domain.interfaces;

import com.badice.domain.entities.Direction;
import com.badice.domain.entities.Position;

/**
 * Interfaz para entidades que pueden moverse en el mapa.
 */
public interface Movable {
    /**
     * Mueve la entidad en la dirección especificada.
     */
    void move(Direction direction);

    /**
     * Obtiene la posición actual de la entidad.
     */
    Position getPosition();

    /**
     * Establece la posición de la entidad.
     */
    void setPosition(Position position);

    /**
     * Obtiene la velocidad de movimiento de la entidad.
     */
    int getSpeed();
}
