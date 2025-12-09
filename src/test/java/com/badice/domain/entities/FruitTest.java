package com.badice.domain.entities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas para las diferentes clases de frutas.
 */
public class FruitTest {
    private Fruit basicFruit;
    private Fruit cherryFruit;
    private Fruit cactusFruit;
    private Player player;
    private GameMap testMap;

    @BeforeEach
    public void setUp() {
        testMap = new GameMap(15, 11);
        player = new Player(new Position(5, 5));
        basicFruit = new BasicFruit(new Position(3, 3), "banana", 100);
        cherryFruit = new CherryFruit(new Position(7, 7), testMap);
        cactusFruit = new CactusFruit(new Position(9, 9));
    }

    @Test
    public void testFruitInitialization() {
        assertFalse(basicFruit.isCollected());
        assertTrue(basicFruit.isActive());
        assertEquals(100, basicFruit.getPoints());
        assertEquals("banana", basicFruit.getFruitType());
    }

    @Test
    public void testFruitCollection() {
        assertFalse(basicFruit.isCollected());
        basicFruit.collect();
        assertTrue(basicFruit.isCollected());
        assertFalse(basicFruit.isActive());
    }

    @Test
    public void testFruitCollisionWithPlayer() {
        Fruit fruit = new BasicFruit(new Position(5, 5), "grape", 50);
        assertTrue(fruit.collidesWith(player));
    }

    @Test
    public void testFruitNotSolid() {
        assertFalse(basicFruit.isSolid());
    }

    @Test
    public void testFruitOnCollision() {
        int initialScore = player.getScore();
        basicFruit.onCollision(player);

        assertTrue(basicFruit.isCollected());
        assertEquals(initialScore + 100, player.getScore());
    }

    @Test
    public void testCherryFruitCreation() {
        assertNotNull(cherryFruit);
        assertEquals(150, cherryFruit.getPoints());
        assertEquals("cherry", cherryFruit.getFruitType());
    }

    @Test
    public void testCactusFruitCreation() {
        assertNotNull(cactusFruit);
        assertEquals(250, cactusFruit.getPoints());
        assertEquals("cactus", cactusFruit.getFruitType());
    }

    @Test
    public void testFruitEntityType() {
        assertEquals("FRUIT", basicFruit.getEntityType());
        assertEquals("FRUIT", cherryFruit.getEntityType());
        assertEquals("FRUIT", cactusFruit.getEntityType());
    }

    @Test
    public void testCollectedFruitDoesNotCollide() {
        basicFruit.collect();
        assertFalse(basicFruit.collidesWith(player));
    }
}
