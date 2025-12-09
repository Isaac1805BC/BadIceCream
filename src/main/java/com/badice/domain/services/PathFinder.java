package com.badice.domain.services;

import com.badice.domain.entities.*;
import java.util.*;

public class PathFinder {
    
    public static Direction getNextStep(Position start, Position target, GameMap map) {
        if (start.equals(target)) return null;

        Queue<Position> queue = new LinkedList<>();
        queue.add(start);
        
        Map<Position, Position> cameFrom = new HashMap<>();
        cameFrom.put(start, null);
        
        // BFS to find target
        Position current = null;
        boolean found = false;
        
        Set<Position> visited = new HashSet<>();
        visited.add(start);

        while (!queue.isEmpty()) {
            current = queue.poll();

            if (current.equals(target)) {
                found = true;
                break;
            }

            for (Direction dir : Direction.values()) {
                Position next = current.move(dir);
                
                // Check bounds and obstacles
                if (isValidMove(next, map) && !visited.contains(next)) { // Using isValidMove from somewhere? Or custom check?
                    // Custom check logic here as we don't have access to MovementService directly
                    // But we can check map entities.
                    // Actually, bots can move through powerups but not walls/ice usually.
                    // Let's assume basic collision logic: no Wall, no IceBlock.
                    if (!isBlocked(next, map)) {
                        queue.add(next);
                        visited.add(next);
                        cameFrom.put(next, current);
                    }
                }
            }
        }

        if (!found) return null; // No path found

        // Backtrack to find the first step
        Position step = target;
        while (cameFrom.get(step) != null && !cameFrom.get(step).equals(start)) {
            step = cameFrom.get(step);
        }
        
        // Determine direction from start to step
        return getDirection(start, step);
    }
    
    public static boolean isValidMove(Position pos, GameMap map) {
        return pos.getX() >= 0 && pos.getX() < 15 && pos.getY() >= 0 && pos.getY() < 11; // Hardcoded bounds from GameEngine/Factory
    }

    public static boolean isBlocked(Position pos, GameMap map) {
        // Simple check for blocking entities
        for (GameEntity entity : map.getEntities()) {
            if (entity.getPosition().equals(pos)) {
                if (entity instanceof Block || entity instanceof IceBlock) { // Assuming Block is Wall
                    return true;
                }
            }
        }
        return false;
    }

    private static Direction getDirection(Position from, Position to) {
        if (to.getX() > from.getX()) return Direction.RIGHT;
        if (to.getX() < from.getX()) return Direction.LEFT;
        if (to.getY() > from.getY()) return Direction.DOWN;
        if (to.getY() < from.getY()) return Direction.UP;
        return null;
    }
}
