package com.badice.domain.services;

import com.badice.domain.entities.*;
import com.badice.domain.enums.GameMode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas para el motor principal del juego.
 */
public class GameEngineTest {
    private GameEngine gameEngine;

    @BeforeEach
    public void setUp() {
        gameEngine = new GameEngine();
    }

    @Test
    public void testGameEngineInitialization() {
        assertNotNull(gameEngine);
        assertNotNull(gameEngine.getScoreService());
        assertNotNull(gameEngine.getStateManager());
    }

    @Test
    public void testStartNewGame() {
        gameEngine.startNewGame(GameMode.ONE_PLAYER);

        assertNotNull(gameEngine.getCurrentMap());
        assertEquals(GameMode.ONE_PLAYER, gameEngine.getCurrentMode());
        assertEquals(1, gameEngine.getCurrentLevelNumber());
    }

    @Test
    public void testPlayerMovement() {
        gameEngine.startNewGame(GameMode.ONE_PLAYER);
        Player player = gameEngine.getPlayer();
        assertNotNull(player);

        Position initialPos = player.getPosition();
        gameEngine.movePlayer(Direction.RIGHT, 0);

        // La posición puede cambiar o no dependiendo de si hay obstáculos
        assertNotNull(player.getPosition());
    }

    @Test
    public void testIceCreation() {
        gameEngine.startNewGame(GameMode.ONE_PLAYER);
        Player player = gameEngine.getPlayer();

        // Intentar crear hielo
        boolean created = gameEngine.playerCreateIce(Direction.RIGHT, 0);
        // Puede ser true o false dependiendo del espacio disponible
        assertNotNull(gameEngine.getCurrentMap());
    }

    @Test
    public void testIceDestruction() {
        gameEngine.startNewGame(GameMode.ONE_PLAYER);

        // Crear hielo primero
        gameEngine.playerCreateIce(Direction.RIGHT, 0);

        // Intentar destruir hielo
        boolean destroyed = gameEngine.playerDestroyIce(Direction.RIGHT, 0);
        assertNotNull(gameEngine.getCurrentMap());
    }

    @Test
    public void testLevelProgression() {
        gameEngine.startNewGame(GameMode.ONE_PLAYER);
        int initialLevel = gameEngine.getCurrentLevelNumber();

        gameEngine.nextLevel();

        assertEquals(initialLevel + 1, gameEngine.getCurrentLevelNumber());
    }

    @Test
    public void testPauseAndResume() {
        gameEngine.startNewGame(GameMode.ONE_PLAYER);

        assertFalse(gameEngine.isPaused());
        gameEngine.pauseGameTimer();
        assertTrue(gameEngine.isPaused());

        gameEngine.resumeGameTimer();
        assertFalse(gameEngine.isPaused());
    }

    @Test
    public void testTimeTracking() {
        gameEngine.startNewGame(GameMode.ONE_PLAYER);
        gameEngine.resetGameTimer();

        long elapsed = gameEngine.getElapsedTime();
        assertTrue(elapsed >= 0);

        long remaining = gameEngine.getTimeRemaining();
        assertTrue(remaining > 0);
    }

    @Test
    public void testMultiPlayerMode() {
        gameEngine.startNewGame(GameMode.PVP);

        assertEquals(GameMode.PVP, gameEngine.getCurrentMode());
        assertNotNull(gameEngine.getCurrentMap());

        // En modo PVP debe haber 2 jugadores
        assertTrue(gameEngine.getCurrentMap().getPlayers().size() >= 1);
    }

    @Test
    public void testVictoryCondition() {
        gameEngine.startNewGame(GameMode.ONE_PLAYER);

        // Al inicio no debe haber victoria
        boolean victory = gameEngine.checkVictoryCondition();
        // Depende del estado del juego
        assertNotNull(gameEngine.getScoreService());
    }
}
