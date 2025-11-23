package com.badice.domain.services;

import com.badice.domain.entities.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests para CollisionDetector.
 */
class CollisionDetectorTest {
    private CollisionDetector collisionDetector;
    private GameMap gameMap;
    private Player player;

    @BeforeEach
    void setUp() {
        collisionDetector = new CollisionDetector();
        gameMap = new GameMap(10, 10, 32);
        player = new Player(new Position(5, 5));
        gameMap.setPlayer(player);
    }

    @Test
    void testDetectPlayerCollisionWithFruit() {
        Fruit fruit = new Fruit(new Position(5, 5), "apple", 100);
        gameMap.addEntity(fruit);

        var collisions = collisionDetector.detectPlayerCollisions(player, gameMap);
        assertEquals(1, collisions.size());
        assertEquals(fruit, collisions.get(0));
    }

    @Test
    void testDetectPlayerCollisionWithEnemy() {
        Enemy enemy = new Enemy(new Position(5, 5), null, "basic");
        gameMap.addEntity(enemy);

        assertTrue(collisionDetector.detectEnemyPlayerCollision(enemy, player));
    }

    @Test
    void testNoCollisionWhenApart() {
        Enemy enemy = new Enemy(new Position(8, 8), null, "basic");
        gameMap.addEntity(enemy);

        assertFalse(collisionDetector.detectEnemyPlayerCollision(enemy, player));
    }

    @Test
    void testWillCollideWithSolid() {
        gameMap.addEntity(new Block(new Position(6, 5), "wall"));

        assertTrue(collisionDetector.willCollideWithSolid(new Position(6, 5), gameMap));
        assertFalse(collisionDetector.willCollideWithSolid(new Position(7, 5), gameMap));
    }
}
