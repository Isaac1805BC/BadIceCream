package com.badice.domain.interfaces;

import com.badice.domain.entities.GameEntity;
import com.badice.domain.entities.GameMap;
import com.badice.domain.entities.Direction;
import com.badice.domain.entities.Enemy;
import java.io.Serializable;

/**
 * Interfaz Strategy para definir patrones de movimiento de enemigos.
 */
public interface MovementPattern extends Serializable {
    /**
     * Calcula la siguiente dirección de movimiento para el enemigo.
     * 
     * @param enemy El enemigo que se mueve
     * @param map   El mapa del juego
     * @return La dirección en la que debe moverse el enemigo
     */
    Direction calculateNextDirection(Enemy enemy, GameMap map);

    /**
     * Actualiza el estado interno del patrón (si es necesario).
     */
    void update();

    /**
     * Reinicia el patrón a su estado inicial.
     */
    void reset();

    /**
     * Establece la entidad que usa este patrón.
     */
    default void setEntity(GameEntity entity) {
    }

    /**
     * Establece el mapa del juego para verificar colisiones.
     */
    default void setMap(GameMap map) {
    }
}
