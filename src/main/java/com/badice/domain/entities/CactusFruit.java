package com.badice.domain.entities;

/**
 * Fruta Cactus: Alterna entre estado seguro y peligroso cada 30 segundos.
 */
public class CactusFruit extends Fruit {
    private static final long STATE_CHANGE_INTERVAL = 30000; // 30 segundos
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
            System.out.println("Cactus state changed. Dangerous: " + isDangerous);
        }
    }

    public boolean isDangerous() {
        return isDangerous;
    }

    @Override
    public void collect() {
        if (!isDangerous) {
            super.collect();
        }
    }
}
