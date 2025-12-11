package com.badice.domain.entities;

/**
 * Baldosa Caliente: Superficie especial que derrite bloques de hielo
 * inmediatamente.
 * NO daña jugadores ni enemigos, solo derrite hielo.
 */
public class HotTile extends Block {
    private static final String ENTITY_TYPE = "HOT_TILE";

    public HotTile(Position position) {
        super(position, "hot_tile");
    }
    
    @Override
    public void accept(com.badice.domain.interfaces.EntityVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    protected void doUpdate() {
        // Las baldosas calientes no necesitan actualización
    }

    @Override
    public boolean isSolid() {
        // Las baldosas calientes NO bloquean el movimiento
        // Los jugadores y enemigos pueden caminar sobre ellas
        return false;
    }

    @Override
    public void onCollision(GameEntity other) {
        // No hace nada en colisión - el derretimiento de hielo
        // se maneja en IceManager cuando se intenta crear hielo
    }

    @Override
    public String getEntityType() {
        return ENTITY_TYPE;
    }

    /**
     * Verifica si esta baldosa derrite hielo.
     * 
     * @return Siempre true para HotTile
     */
    public boolean meltsIce() {
        return true;
    }
}
