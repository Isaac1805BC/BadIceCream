package com.badice.domain.entities;

/**
 * Fogata: Obstáculo que elimina helados al contacto.
 * NO daña enemigos.
 * Se apaga temporalmente cuando se le coloca un bloque de hielo encima (vuelve
 * a encenderse en 10 seg).
 */
public class Campfire extends Block {
    private static final long EXTINGUISH_DURATION = 10000; // 10 segundos
    private static final String ENTITY_TYPE = "CAMPFIRE";

    private boolean isLit; // true = encendida (peligrosa), false = apagada (segura)
    private long extinguishedTime; // Momento en que se apagó

    public Campfire(Position position) {
        super(position, "campfire");
        this.isLit = true; // Empieza encendida
        this.extinguishedTime = 0;
    }

    public boolean isLit() {
        return isLit;
    }

    @Override
    protected void doUpdate() {
        // Si está apagada, verificar si debe volver a encenderse
        if (!isLit && extinguishedTime > 0) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - extinguishedTime >= EXTINGUISH_DURATION) {
                relight();
            }
        }
    }

    /**
     * Apaga la fogata temporalmente.
     */
    public void extinguish() {
        if (isLit) {
            this.isLit = false;
            this.extinguishedTime = System.currentTimeMillis();
        }
    }

    /**
     * Vuelve a encender la fogata.
     */
    private void relight() {
        this.isLit = true;
        this.extinguishedTime = 0;
    }

    @Override
    public void onCollision(GameEntity other) {
        // Solo daña jugadores cuando está encendida
        if (isLit && other instanceof Player) {
            // La lógica de eliminación del jugador se maneja en CollisionDetector
        }
        // NO daña enemigos
    }

    @Override
    public String getEntityType() {
        return ENTITY_TYPE;
    }

    /**
     * Obtiene el tiempo restante hasta que la fogata se vuelva a encender.
     * 
     * @return Milisegundos restantes, o 0 si está encendida
     */
    public long getTimeUntilRelight() {
        if (isLit) {
            return 0;
        }
        long elapsed = System.currentTimeMillis() - extinguishedTime;
        long remaining = EXTINGUISH_DURATION - elapsed;
        return Math.max(0, remaining);
    }
}
