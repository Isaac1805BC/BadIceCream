package com.badice.domain.entities;

import java.util.UUID;
import java.io.Serializable;

/**
 * Clase abstracta base para todas las entidades del juego.
 * Implementa el patrón Template Method para comportamientos comunes.
 */
public abstract class GameEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    protected final String id;
    protected Position position;
    protected boolean active;

    public GameEntity(Position position) {
        this.id = UUID.randomUUID().toString();
        this.position = position;
        this.active = true;
    }

    public String getId() {
        return id;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Template Method: actualiza la entidad en cada frame.
     * Las subclases pueden sobrescribir este método para añadir comportamiento
     * específico.
     */
    public void update() {
        if (!active) {
            return;
        }
        doUpdate();
    }

    /**
     * Método abstracto que las subclases deben implementar para su lógica de
     * actualización.
     */
    protected abstract void doUpdate();

    /**
     * Devuelve el tipo de entidad (para renderizado y colisiones).
     */
    public abstract String getEntityType();

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        GameEntity that = (GameEntity) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
