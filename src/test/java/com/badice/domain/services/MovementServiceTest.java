package com.badice.domain.services;

import com.badice.domain.entities.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests para MovementService.
 */
class MovementServiceTest {
    private MovementService movementService;
    private CollisionDetector collisionDetector;
    private GameMap gameMap;
    private Player player;

    @BeforeEach
    void setUp() {
        collisionDetector = new CollisionDetector();
        movementService = new MovementService(collisionDetector);
        gameMap = new GameMap(10, 10, 32);
        player = new Player(new Position(5, 5));
        gameMap.setPlayer(player);
    }

    @Test
    void testValidMovement() {
        boolean moved = movementService.moveEntity(player, Direction.RIGHT, gameMap);
        assertTrue(moved);
        assertEquals(new Position(6, 5), player.getPosition());
    }

    @Test
    void testMovementBlockedByWall() {
        // Añadir un bloque a la derecha del jugador
        gameMap.addEntity(new Block(new Position(6, 5), "wall"));

        boolean moved = movementService.moveEntity(player, Direction.RIGHT, gameMap);
        assertFalse(moved);
        assertEquals(new Position(5, 5), player.getPosition());
    }

    @Test
    void testMovementOutOfBounds() {
        Player edgePlayer = new Player(new Position(0, 0));
        gameMap.setPlayer(edgePlayer);

        boolean moved = movementService.moveEntity(edgePlayer, Direction.LEFT, gameMap);
        assertFalse(moved);
        assertEquals(new Position(0, 0), edgePlayer.getPosition());
    }

    @Test
    void testCanMoveCheck() {
        assertTrue(movementService.canMove(player, Direction.UP, gameMap));

        gameMap.addEntity(new Block(new Position(5, 4), "wall"));
        assertFalse(movementService.canMove(player, Direction.UP, gameMap));
    }
}
