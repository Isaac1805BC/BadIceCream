package com.badice.domain.interfaces;

import com.badice.domain.entities.*;
import com.badice.domain.entities.HotTile;

public interface EntityVisitor {
    void visit(Player player);
    
    // Enemies
    void visit(BasicEnemy enemy);
    void visit(TrollEnemy enemy);
    void visit(PotEnemy enemy);
    void visit(SquidEnemy enemy);
    void visit(NarvalEnemy enemy);
    
    // Fruits
    void visit(BasicFruit fruit);
    void visit(CherryFruit fruit);
    void visit(PineappleFruit fruit);
    void visit(CactusFruit fruit);
    
    // Blocks
    void visit(Block block);
    void visit(IceBlock block);
    void visit(Campfire block);
    void visit(HotTile block);
}
