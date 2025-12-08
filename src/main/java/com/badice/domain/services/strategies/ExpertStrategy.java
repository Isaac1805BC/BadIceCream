package com.badice.domain.services.strategies;

import com.badice.domain.entities.*;
import com.badice.domain.interfaces.BotStrategy;
import java.util.List;

public class ExpertStrategy implements BotStrategy {
    private final HungryStrategy hungry = new HungryStrategy();
    private final FearfulStrategy fearful = new FearfulStrategy();
    
    @Override
    public Direction calculateNextMove(Player bot, GameMap map) {
        Position botPos = bot.getPosition();
        List<Enemy> enemies = map.getEnemies();
        
        double minEnemyDist = Double.MAX_VALUE;
        for (Enemy enemy : enemies) {
            if (enemy.isActive()) {
                double dist = botPos.distanceTo(enemy.getPosition());
                if (dist < minEnemyDist) {
                    minEnemyDist = dist;
                }
            }
        }
        
        // Si hay un enemigo muy cerca (menos de 3 bloques), huir
        if (minEnemyDist < 3.0) {
            return fearful.calculateNextMove(bot, map);
        }
        
        // Si no, buscar comida
        return hungry.calculateNextMove(bot, map);
    }
}
