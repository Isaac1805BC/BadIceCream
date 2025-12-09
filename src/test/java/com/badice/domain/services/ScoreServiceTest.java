package com.badice.domain.services;

import com.badice.domain.entities.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas para el servicio de puntuación.
 */
public class ScoreServiceTest {
    private ScoreService scoreService;

    @BeforeEach
    public void setUp() {
        scoreService = new ScoreService();
    }

    @Test
    public void testInitialScore() {
        assertEquals(0, scoreService.getCurrentScore());
        assertEquals(1, scoreService.getCurrentLevel());
    }

    @Test
    public void testAddScore() {
        scoreService.addScore(100);
        assertEquals(100, scoreService.getCurrentScore());

        scoreService.addScore(50);
        assertEquals(150, scoreService.getCurrentScore());
    }

    @Test
    public void testResetScore() {
        scoreService.addScore(500);
        assertEquals(500, scoreService.getCurrentScore());

        scoreService.resetCurrentScore();
        assertEquals(0, scoreService.getCurrentScore());
    }

    @Test
    public void testLevelProgression() {
        assertEquals(1, scoreService.getCurrentLevel());

        scoreService.nextLevel();
        assertEquals(2, scoreService.getCurrentLevel());

        scoreService.setCurrentLevel(5);
        assertEquals(5, scoreService.getCurrentLevel());
    }

    @Test
    public void testFruitTracking() {
        scoreService.setTotalFruits(10);
        assertEquals(0, scoreService.getFruitsCollected());

        scoreService.collectFruit();
        assertEquals(1, scoreService.getFruitsCollected());

        scoreService.collectFruit();
        scoreService.collectFruit();
        assertEquals(3, scoreService.getFruitsCollected());
    }

    @Test
    public void testAllFruitsCollected() {
        scoreService.setTotalFruits(5);
        assertFalse(scoreService.areAllFruitsCollected());

        for (int i = 0; i < 5; i++) {
            scoreService.collectFruit();
        }

        assertTrue(scoreService.areAllFruitsCollected());
    }

    @Test
    public void testPhaseProgression() {
        scoreService.setTotalFruits(3);
        scoreService.collectFruit();
        scoreService.collectFruit();
        scoreService.collectFruit();

        assertTrue(scoreService.areAllFruitsCollected());

        // Nueva fase
        scoreService.nextPhase(5);
        assertEquals(5, scoreService.getTotalFruits());
        assertEquals(0, scoreService.getFruitsCollected());
    }

    @Test
    public void testSetFruitsCollected() {
        scoreService.setTotalFruits(10);
        scoreService.setFruitsCollected(7);

        assertEquals(7, scoreService.getFruitsCollected());
        assertFalse(scoreService.areAllFruitsCollected());
    }
}
