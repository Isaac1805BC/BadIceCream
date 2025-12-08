package com.badice.domain.services.strategies;

import com.badice.domain.entities.*;
import com.badice.domain.interfaces.BotStrategy;
import java.util.List;

public class FearfulStrategy implements BotStrategy {
    @Override
    public Direction calculateNextMove(Player bot, GameMap map) {
        Position botPos = bot.getPosition();
        List<Enemy> enemies = map.getEnemies();
        
        Position nearestEnemy = null;
        double minDistance = Double.MAX_VALUE;
        
        // Buscar enemigo más cercano
        for (Enemy enemy : enemies) {
            if (enemy.isActive()) {
                double dist = botPos.distanceTo(enemy.getPosition());
                if (dist < minDistance) {
                    minDistance = dist;
                    nearestEnemy = enemy.getPosition();
                }
            }
        }
        
        if (nearestEnemy == null) {
            return Direction.values()[(int)(Math.random() * 4)]; // Random si no hay enemigos
        }
        
        // Moverse en dirección opuesta
        return getDirectionAwayFrom(botPos, nearestEnemy);
    }
    
    private Direction getDirectionAwayFrom(Position from, Position to) {
        int dx = to.getX() - from.getX();
        int dy = to.getY() - from.getY();
        
        // Invertir lógica para huir
        if (Math.abs(dx) > Math.abs(dy)) {
            return dx > 0 ? Direction.LEFT : Direction.RIGHT;
        } else {
            return dy > 0 ? Direction.UP : Direction.DOWN;
        }
    }
}
