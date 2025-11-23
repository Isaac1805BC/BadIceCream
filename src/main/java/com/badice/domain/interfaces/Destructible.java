package com.badice.domain.interfaces;

/**
 * Interfaz para entidades que pueden ser destruidas.
 */
public interface Destructible {
    /**
     * Destruye la entidad.
     */
    void destroy();

    /**
     * Verifica si la entidad está destruida.
     */
    boolean isDestroyed();

    /**
     * Obtiene los puntos de vida de la entidad.
     */
    int getHealth();

    /**
     * Reduce los puntos de vida de la entidad.
     */
    void takeDamage(int damage);
}
