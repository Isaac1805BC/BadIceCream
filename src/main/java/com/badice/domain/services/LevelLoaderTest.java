package com.badice.domain.services;

import com.badice.domain.config.LevelConfig;
import com.badice.domain.config.LevelConfig.PhaseConfig;
import com.badice.domain.config.LevelConfig.EntityConfig;
import java.io.File;

public class LevelLoaderTest {
    public static void main(String[] args) {
        try {
            System.out.println("Testing LevelLoader...");
            File levelFile = new File("levels/level1.txt");
            System.out.println("Level file absolute path: " + levelFile.getAbsolutePath());
            System.out.println("File exists: " + levelFile.exists());

            LevelLoader loader = new LevelLoader();
            // Since loadLevel expects a number and appends "levels/level" + n + ".txt",
            // we should ensure it can find our file. 
            // The file is at c:\Users\samue\Trabajos_ECI\DOPO\BadIceCream\levels\level1.txt
            // The working directory for execution might be root.
            
            LevelConfig config = loader.loadLevel(1);
            
            System.out.println("Total Phases: " + config.getTotalPhases());
            System.out.println("Parsed Phases Count: " + config.getPhases().size());
            
            int phaseIndex = 0;
            for (PhaseConfig phase : config.getPhases()) {
                System.out.println("Phase " + (phaseIndex + 1) + " (ID: " + phase.getPhaseNumber() + ")");
                int fruits = 0;
                for (EntityConfig entity : phase.getEntities()) {
                    if ("FRUIT".equals(entity.getType())) {
                        fruits++;
                    }
                }
                System.out.println("  - Fruits: " + fruits);
                System.out.println("  - Total Entities: " + phase.getEntities().size());
                phaseIndex++;
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
