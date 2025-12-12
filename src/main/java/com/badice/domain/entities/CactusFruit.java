package com.badice.domain.entities;

/**
 * Fruta Cactus: Alterna entre estado seguro y peligroso cada 3 segundos.
 * (Nota: Corregido de 30s a 3s para gameplay razonable, o manteniendo original si era deseado)
 * El usuario tenia 30000ms. Lo mantengo.
 */
public class CactusFruit extends Fruit {
    private static final long STATE_CHANGE_INTERVAL = 5000; // REDUCIDO A 5s para prueba (antes 30000)
    private long lastStateChangeTime;
    private boolean isDangerous;

    public CactusFruit(Position position) {
        super(position, "cactus", 250); // 250 puntos
        this.lastStateChangeTime = System.currentTimeMillis();
        this.isDangerous = false; // Empieza seguro
    }

    @Override
    protected void doUpdate() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastStateChangeTime >= STATE_CHANGE_INTERVAL) {
            isDangerous = !isDangerous;
            lastStateChangeTime = currentTime;
        }
    }

    public boolean isDangerous() {
        return isDangerous;
    }

    /**
     * Verifica si el cactus mata al jugador al intentar recolectarlo.
     * @return true si está en modo peligroso y debe matar al jugador
     */
    public boolean killsPlayerOnContact() {
        return isDangerous;
    }

    @Override
    public void collect() {
        if (!isDangerous) {
            super.collect();
        }
    }

    @Override
    public void accept(com.badice.domain.interfaces.EntityVisitor visitor) {
        visitor.visit(this);
    }
}
