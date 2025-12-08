package com.badice.domain.interfaces;

import com.badice.domain.entities.Player;
import com.badice.domain.entities.GameMap;
import com.badice.domain.entities.Direction;

public interface BotStrategy {
    Direction calculateNextMove(Player bot, GameMap map);
}
