package com.badice.domain.services;

import com.badice.domain.entities.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests para IceManager.
 */
class IceManagerTest {
    private IceManager iceManager;
    private GameMap gameMap;

    @BeforeEach
    void setUp() {
        iceManager = new IceManager();
        gameMap = new GameMap(10, 10, 32);
    }

    @Test
    void testCreateIceBlock() {
        Position startPos = new Position(5, 5);
        IceBlock ice = iceManager.createIceBlock(startPos, Direction.RIGHT, gameMap);

        assertNotNull(ice);
        // El IceManager crea bloques hasta el borde del mapa, el último es en x=9
        assertEquals(new Position(9, 5), ice.getPosition());
        assertTrue(ice.isActive());
    }

    @Test
    void testCannotCreateIceOutOfBounds() {
        Position edgePos = new Position(9, 5);
        IceBlock ice = iceManager.createIceBlock(edgePos, Direction.RIGHT, gameMap);

        assertNull(ice);
    }

    @Test
    void testCannotCreateIceOnOccupiedSpace() {
        gameMap.addEntity(new Block(new Position(6, 5), "wall"));

        Position startPos = new Position(5, 5);
        IceBlock ice = iceManager.createIceBlock(startPos, Direction.RIGHT, gameMap);

        assertNull(ice);
    }

    @Test
    void testDestroyIceBlock() {
        Position icePos = new Position(6, 5);
        gameMap.addIceBlock(icePos);

        boolean destroyed = iceManager.destroyIceBlock(new Position(5, 5), Direction.RIGHT, gameMap);
        assertTrue(destroyed);
    }

    @Test
    void testHasIceBlockAt() {
        Position icePos = new Position(6, 5);
        gameMap.addIceBlock(icePos);

        assertTrue(iceManager.hasIceBlockAt(icePos, gameMap));
        assertFalse(iceManager.hasIceBlockAt(new Position(7, 5), gameMap));
    }
}
