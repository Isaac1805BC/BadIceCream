package com.badice.domain.services.strategies;

import com.badice.domain.entities.*;
import com.badice.domain.interfaces.BotStrategy;
import java.util.List;

public class HungryStrategy implements BotStrategy {
    @Override
    public Direction calculateNextMove(Player bot, GameMap map) {
        Position botPos = bot.getPosition();
        List<GameEntity> entities = map.getEntities();
        
        Position nearestFruit = null;
        double minDistance = Double.MAX_VALUE;
        
        // Buscar fruta más cercana
        for (GameEntity entity : entities) {
            if (entity instanceof Fruit && !((Fruit) entity).isCollected()) {
                double dist = botPos.distanceTo(entity.getPosition());
                if (dist < minDistance) {
                    minDistance = dist;
                    nearestFruit = entity.getPosition();
                }
            }
        }
        
        if (nearestFruit == null) {
            return Direction.values()[(int)(Math.random() * 4)];
        }
        
        // Usar BFS para encontrar el camino
        com.badice.domain.entities.Direction nextDir = com.badice.domain.services.PathFinder.getNextStep(botPos, nearestFruit, map);
        
        if (nextDir != null) {
            return nextDir;
        }
        
        // Fallback si no encuentra camino
        return getDirectionTowards(botPos, nearestFruit);
    }
    
    private Direction getDirectionTowards(Position from, Position to) {
        int dx = to.getX() - from.getX();
        int dy = to.getY() - from.getY();
        
        if (Math.abs(dx) > Math.abs(dy)) {
            return dx > 0 ? Direction.RIGHT : Direction.LEFT;
        } else {
            return dy > 0 ? Direction.DOWN : Direction.UP;
        }
    }
}
