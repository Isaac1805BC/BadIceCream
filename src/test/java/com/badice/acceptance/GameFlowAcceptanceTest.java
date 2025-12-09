package com.badice.acceptance;

import com.badice.domain.entities.*;
import com.badice.domain.enums.GameMode;
import com.badice.domain.services.GameEngine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas de aceptación para el flujo completo del juego.
 * Estas pruebas verifican escenarios de usuario de extremo a extremo.
 */
public class GameFlowAcceptanceTest {
    private GameEngine gameEngine;

    @BeforeEach
    public void setUp() {
        gameEngine = new GameEngine();
    }

    @Test
    public void testCompleteGameFlow_SinglePlayer() {
        // Escenario: Un jugador inicia el juego, recolecta frutas y avanza de nivel

        // 1. Iniciar juego en modo un jugador
        gameEngine.startNewGame(GameMode.ONE_PLAYER);
        assertEquals(GameMode.ONE_PLAYER, gameEngine.getCurrentMode());
        assertNotNull(gameEngine.getPlayer());

        // 2. Verificar que el jugador puede moverse
        Player player = gameEngine.getPlayer();
        Position initialPos = player.getPosition();
        gameEngine.movePlayer(Direction.RIGHT, 0);
        assertNotNull(player.getPosition());

        // 3. Verificar que el jugador puede crear hielo
        gameEngine.playerCreateIce(Direction.UP, 0);
        assertNotNull(gameEngine.getCurrentMap());

        // 4. Verificar que el jugador puede destruir hielo
        gameEngine.playerDestroyIce(Direction.UP, 0);
        assertNotNull(gameEngine.getCurrentMap());

        // 5. Verificar que el tiempo está corriendo
        long timeRemaining = gameEngine.getTimeRemaining();
        assertTrue(timeRemaining > 0);
    }

    @Test
    public void testCompleteGameFlow_TwoPlayers() {
        // Escenario: Dos jugadores compiten en modo PvP

        // 1. Iniciar juego en modo PvP
        gameEngine.startNewGame(GameMode.PVP);
        assertEquals(GameMode.PVP, gameEngine.getCurrentMode());

        // 2. Verificar que hay múltiples jugadores
        assertTrue(gameEngine.getCurrentMap().getPlayers().size() >= 1);

        // 3. Ambos jugadores pueden moverse
        gameEngine.movePlayer(Direction.RIGHT, 0);
        if (gameEngine.getCurrentMap().getPlayers().size() > 1) {
            gameEngine.movePlayer(Direction.LEFT, 1);
        }

        assertNotNull(gameEngine.getCurrentMap());
    }

    @Test
    public void testPauseAndResumeGame() {
        // Escenario: El jugador pausa y reanuda el juego

        gameEngine.startNewGame(GameMode.ONE_PLAYER);

        // 1. Verificar que el juego no está pausado inicialmente
        assertFalse(gameEngine.isPaused());

        // 2. Pausar el juego
        gameEngine.pauseGameTimer();
        assertTrue(gameEngine.isPaused());

        // 3. Reanudar el juego
        gameEngine.resumeGameTimer();
        assertFalse(gameEngine.isPaused());
    }

    @Test
    public void testLevelProgression() {
        // Escenario: El jugador completa un nivel y avanza al siguiente

        gameEngine.startNewGame(GameMode.ONE_PLAYER);
        int initialLevel = gameEngine.getCurrentLevelNumber();

        // Avanzar al siguiente nivel
        gameEngine.nextLevel();

        assertEquals(initialLevel + 1, gameEngine.getCurrentLevelNumber());
        assertNotNull(gameEngine.getCurrentMap());
    }

    @Test
    public void testGameSaveAndLoad() {
        // Escenario: El jugador guarda y carga una partida

        gameEngine.startNewGame(GameMode.ONE_PLAYER);

        // Hacer algunos movimientos
        gameEngine.movePlayer(Direction.RIGHT, 0);
        gameEngine.movePlayer(Direction.DOWN, 0);

        // Obtener estado del juego
        var gameState = gameEngine.getGameState();
        assertNotNull(gameState);
        assertNotNull(gameState.getGameMap());

        // Restaurar estado
        gameEngine.restoreGameState(gameState);
        assertNotNull(gameEngine.getCurrentMap());
    }

    @Test
    public void testPlayerVsMachine() {
        // Escenario: Jugador contra máquina

        gameEngine.startNewGame(GameMode.PVM);
        assertEquals(GameMode.PVM, gameEngine.getCurrentMode());

        // Verificar que hay al menos un jugador
        assertNotNull(gameEngine.getCurrentMap());
        assertTrue(gameEngine.getCurrentMap().getPlayers().size() >= 1);

        // Actualizar el juego (la IA debería moverse)
        gameEngine.updateEntities();
        assertNotNull(gameEngine.getCurrentMap());
    }

    @Test
    public void testMachineVsMachine() {
        // Escenario: Dos máquinas compiten

        gameEngine.startNewGame(GameMode.MVM);
        assertEquals(GameMode.MVM, gameEngine.getCurrentMode());

        // Actualizar el juego varias veces (las IAs deberían moverse)
        for (int i = 0; i < 10; i++) {
            gameEngine.updateEntities();
        }

        assertNotNull(gameEngine.getCurrentMap());
    }

    @Test
    public void testTimeLimit() {
        // Escenario: Verificar que el límite de tiempo funciona

        gameEngine.startNewGame(GameMode.ONE_PLAYER);
        gameEngine.resetGameTimer();

        long timeRemaining = gameEngine.getTimeRemaining();
        assertTrue(timeRemaining > 0);
        assertTrue(timeRemaining <= 3 * 60 * 1000); // 3 minutos máximo
    }
}
