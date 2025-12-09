package com.badice.domain.entities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas para la clase Enemy y sus subclases.
 */
public class EnemyTest {
    private Enemy trollEnemy;
    private Enemy potEnemy;
    private GameMap testMap;

    @BeforeEach
    public void setUp() {
        testMap = new GameMap(15, 11);
        trollEnemy = new TrollEnemy(new Position(5, 5));
        potEnemy = new PotEnemy(new Position(7, 7));
    }

    @Test
    public void testEnemyInitialization() {
        assertTrue(trollEnemy.isActive());
        assertEquals(5, trollEnemy.getPosition().getX());
        assertEquals(5, trollEnemy.getPosition().getY());
        assertEquals("ENEMY", trollEnemy.getEntityType());
    }

    @Test
    public void testEnemyMovement() {
        Position originalPos = trollEnemy.getPosition();
        trollEnemy.move(Direction.RIGHT);

        assertNotEquals(originalPos, trollEnemy.getPosition());
        assertEquals(originalPos.getX() + 1, trollEnemy.getPosition().getX());
    }

    @Test
    public void testEnemyCollisionWithPlayer() {
        Player player = new Player(new Position(5, 5));

        assertTrue(trollEnemy.collidesWith(player));
        assertTrue(trollEnemy.isSolid());
    }

    @Test
    public void testEnemySpeed() {
        assertTrue(trollEnemy.getSpeed() > 0);
        assertEquals(1, trollEnemy.getSpeed());
    }

    @Test
    public void testEnemyDeactivation() {
        trollEnemy.setActive(false);
        assertFalse(trollEnemy.isActive());
    }

    @Test
    public void testTrollEnemyCreation() {
        assertNotNull(trollEnemy);
        assertTrue(trollEnemy instanceof TrollEnemy);
    }

    @Test
    public void testPotEnemyCreation() {
        assertNotNull(potEnemy);
        assertTrue(potEnemy instanceof PotEnemy);
    }
}
