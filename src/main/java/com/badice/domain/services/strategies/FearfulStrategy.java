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
            return Direction.values()[(int)(Math.random() * 4)];
        }
        
        // Lógica mejorada: Moverse a la posición adyacente que maximice la distancia al enemigo
        Direction bestDir = null;
        double maxDist = -1;
        
        for (Direction dir : Direction.values()) {
            Position nextPos = botPos.move(dir);
            // Verificar si es movida válida (básico)
            if (!com.badice.domain.services.PathFinder.isValidMove(nextPos, map) || com.badice.domain.services.PathFinder.isBlocked(nextPos, map)) {
                continue;
            }
            
            double dist = nextPos.distanceTo(nearestEnemy);
            if (dist > maxDist) {
                maxDist = dist;
                bestDir = dir;
            }
        }
        
        return bestDir != null ? bestDir : Direction.values()[(int)(Math.random() * 4)];
    }
    

}
