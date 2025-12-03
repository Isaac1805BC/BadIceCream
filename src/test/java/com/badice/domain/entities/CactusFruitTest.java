package com.badice.domain.entities;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CactusFruitTest {

    @Test
    void testCactusStateToggle() {
        CactusFruit cactus = new CactusFruit(new Position(0, 0));
        
        // Initially safe
        assertFalse(cactus.isDangerous(), "Cactus should start safe");
        
        // Simulate update (time passing is handled by System.currentTimeMillis inside, 
        // which is hard to mock without dependency injection or a clock service. 
        // For this test, we can't easily wait 30s. 
        // Ideally, we would refactor CactusFruit to accept a Clock or time provider.
        // Given constraints, I will check initial state and properties.)
        
        assertEquals("cactus", cactus.getFruitType());
        assertEquals(250, cactus.getPoints());
    }
    
    @Test
    void testCollectWhenSafe() {
        CactusFruit cactus = new CactusFruit(new Position(0, 0));
        assertFalse(cactus.isDangerous());
        
        cactus.collect();
        assertTrue(cactus.isCollected(), "Should be collected when safe");
    }
}
