package com.badice.domain.factories;

import com.badice.domain.entities.*;
import com.badice.domain.interfaces.MovementPattern;
import com.badice.domain.patterns.*;

/**
 * Factory para crear diferentes tipos de entidades del juego.
 */
public class EntityFactory {

    /**
     * Crea un jugador en la posición especificada.
     */
    public static Player createPlayer(int x, int y) {
        return new Player(new Position(x, y));
    }

    /**
     * Crea un enemigo con un patrón de movimiento específico.
     */
    public static Enemy createEnemy(int x, int y, String movementType, String enemyType) {
        Position position = new Position(x, y);
        MovementPattern pattern = createMovementPattern(movementType);
        return new BasicEnemy(position, pattern);
    }

    /**
     * Crea un enemigo con patrón horizontal por defecto.
     */
    public static Enemy createEnemy(int x, int y, String enemyType) {
        return createEnemy(x, y, "horizontal", enemyType);
    }

    /**
     * Crea una fruta básica en la posición especificada.
     */
    public static BasicFruit createBasicFruit(int x, int y, String fruitType, int points) {
        return new BasicFruit(new Position(x, y), fruitType, points);
    }

    /**
     * Crea una fruta básica con puntos por defecto (100).
     */
    public static BasicFruit createBasicFruit(int x, int y, String fruitType) {
        return createBasicFruit(x, y, fruitType, 100);
    }

    /**
     * Crea una fruta (alias para mantener compatibilidad).
     */
    public static Fruit createFruit(int x, int y, String fruitType, int points) {
        return createBasicFruit(x, y, fruitType, points);
    }

    /**
     * Crea una fruta con puntos por defecto (100).
     */
    public static Fruit createFruit(int x, int y, String fruitType) {
        return createBasicFruit(x, y, fruitType, 100);
    }

    /**
     * Crea una cereza (se teletransporta cada 20 segundos).
     */
    public static CherryFruit createCherryFruit(int x, int y, GameMap gameMap) {
        return new CherryFruit(new Position(x, y), gameMap);
    }

    /**
     * Crea una piña (se mueve cuando el jugador se mueve).
     */
    public static PineappleFruit createPineappleFruit(int x, int y, Player player, GameMap gameMap) {
        return new PineappleFruit(new Position(x, y), player, gameMap);
    }

    /**
     * Crea una fruta cactus (alterna estado seguro/peligroso).
     */
    public static CactusFruit createCactusFruit(int x, int y) {
        return new CactusFruit(new Position(x, y));
    }

    /**
     * Crea un bloque sólido.
     */
    public static Block createBlock(int x, int y, String blockType) {
        return new Block(new Position(x, y), blockType);
    }

    /**
     * Crea un bloque de pared por defecto.
     */
    public static Block createWall(int x, int y) {
        return createBlock(x, y, "wall");
    }

    /**
     * Crea un bloque de hielo.
     */
    public static IceBlock createIceBlock(int x, int y) {
        return new IceBlock(new Position(x, y));
    }

    /**
     * Crea una fogata (campfire).
     */
    public static Campfire createCampfire(int x, int y) {
        return new Campfire(new Position(x, y));
    }

    /**
     * Crea una baldosa caliente (hot tile).
     */
    public static HotTile createHotTile(int x, int y) {
        return new HotTile(new Position(x, y));
    }

    /**
     * Crea el enemigo Troll - Enemigo del nivel 1.
     */
    public static Enemy createTrollEnemy(int x, int y) {
        return new TrollEnemy(new Position(x, y), new HorizontalMovementPattern());
    }

    /**
     * Crea el enemigo Maceta (Pot) - Enemigo del nivel 2.
     * Persigue al jugador pero no puede romper hielo.
     */
    public static Enemy createPotEnemy(int x, int y) {
        return new PotEnemy(new Position(x, y));
    }

    /**
     * Crea un enemigo calamar (persigue y rompe hielo).
     */
    public static SquidEnemy createSquidEnemy(int x, int y, GameMap map) {
        // Usa ChaseMovementPattern por defecto
        return new SquidEnemy(new Position(x, y), new ChaseMovementPattern());
    }

    /**
     * Crea un enemigo narval (embiste al alinearse con el jugador).
     */
    public static NarvalEnemy createNarvalEnemy(int x, int y) {
        // Usa movimiento horizontal/vertical aleatorio
        return new NarvalEnemy(new Position(x, y), new RandomMovementPattern());
    }

    /**
     * Crea un patrón de movimiento según el tipo especificado.
     */
    public static MovementPattern createMovementPattern(String type) {
        return switch (type.toLowerCase()) {
            case "horizontal" -> new HorizontalMovementPattern();
            case "vertical" -> new VerticalMovementPattern();
            case "circular" -> new CircularMovementPattern();
            case "random" -> new RandomMovementPattern();
            case "chase" -> new ChaseMovementPattern();
            default -> new HorizontalMovementPattern();
        };
    }

    /**
     * Crea un mapa vacío con las dimensiones especificadas.
     */
    public static GameMap createEmptyMap(int width, int height, int cellSize) {
        return new GameMap(width, height, cellSize);
    }

    /**
     * Crea un mapa con dimensiones por defecto.
     */
    public static GameMap createDefaultMap() {
        return new GameMap(15, 11, 32);
    }
}
