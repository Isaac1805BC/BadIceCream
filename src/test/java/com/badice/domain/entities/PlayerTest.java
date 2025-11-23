package com.badice.domain.entities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests para la clase Player.
 */
class PlayerTest {
    private Player player;

    @BeforeEach
    void setUp() {
        player = new Player(new Position(5, 5));
    }

    @Test
    void testPlayerInitialization() {
        assertEquals(new Position(5, 5), player.getPosition());
        assertEquals(3, player.getLives());
        assertEquals(0, player.getScore());
        assertTrue(player.isActive());
    }

    @Test
    void testPlayerMovement() {
        player.move(Direction.UP);
        assertEquals(new Position(5, 4), player.getPosition());

        player.move(Direction.RIGHT);
        assertEquals(new Position(6, 4), player.getPosition());
    }

    @Test
    void testPlayerLoseLife() {
        player.loseLife();
        assertEquals(2, player.getLives());
        assertTrue(player.isActive());

        player.loseLife();
        player.loseLife();
        assertEquals(0, player.getLives());
        assertFalse(player.isActive());
    }

    @Test
    void testPlayerAddLife() {
        player.addLife();
        assertEquals(4, player.getLives());
    }

    @Test
    void testPlayerScore() {
        player.addScore(100);
        assertEquals(100, player.getScore());

        player.addScore(250);
        assertEquals(350, player.getScore());
    }

    @Test
    void testPlayerCollision() {
        Player otherPlayer = new Player(new Position(5, 5));
        assertTrue(player.collidesWith(otherPlayer));

        Player farPlayer = new Player(new Position(10, 10));
        assertFalse(player.collidesWith(farPlayer));
    }
}
