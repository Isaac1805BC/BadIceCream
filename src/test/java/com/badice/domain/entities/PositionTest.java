package com.badice.domain.entities;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests para la clase Position.
 */
class PositionTest {

    @Test
    void testPositionCreation() {
        Position pos = new Position(5, 10);
        assertEquals(5, pos.getX());
        assertEquals(10, pos.getY());
    }

    @Test
    void testPositionEquality() {
        Position pos1 = new Position(3, 7);
        Position pos2 = new Position(3, 7);
        Position pos3 = new Position(4, 7);

        assertEquals(pos1, pos2);
        assertNotEquals(pos1, pos3);
    }

    @Test
    void testPositionMove() {
        Position start = new Position(5, 5);

        Position up = start.move(Direction.UP);
        assertEquals(new Position(5, 4), up);

        Position down = start.move(Direction.DOWN);
        assertEquals(new Position(5, 6), down);

        Position left = start.move(Direction.LEFT);
        assertEquals(new Position(4, 5), left);

        Position right = start.move(Direction.RIGHT);
        assertEquals(new Position(6, 5), right);
    }

    @Test
    void testManhattanDistance() {
        Position pos1 = new Position(0, 0);
        Position pos2 = new Position(3, 4);

        assertEquals(7, pos1.manhattanDistance(pos2));
        assertEquals(7, pos2.manhattanDistance(pos1));
    }

    @Test
    void testEuclideanDistance() {
        Position pos1 = new Position(0, 0);
        Position pos2 = new Position(3, 4);

        assertEquals(5.0, pos1.euclideanDistance(pos2), 0.001);
    }
}
