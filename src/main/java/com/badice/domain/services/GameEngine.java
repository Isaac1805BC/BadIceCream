package com.badice.domain.services;

import com.badice.domain.entities.*;
import com.badice.domain.interfaces.MovementPattern;
import com.badice.domain.states.GameState;
import com.badice.domain.states.MenuState;
import com.badice.domain.factories.EntityFactory;
import com.badice.domain.states.GameOverState;
import java.util.List;

/**
 * Motor principal del juego que orquesta todos los servicios y la lógica del
 * juego.
 */
public class GameEngine {
    // Servicios
    private final MovementService movementService;
    private final CollisionDetector collisionDetector;
    private final IceManager iceManager;
    private final ScoreService scoreService;
    private final GameStateManager stateManager;

    // Estado del juego
    private GameMap currentMap;
    private long gameStartTime;
    private long pausedTime;
    private boolean isPaused;
    private int currentLevelNumber;

    // Control de tiempo
    private long lastUpdateTime;

    public GameEngine() {
        // Inicializar servicios
        this.collisionDetector = new CollisionDetector();
        this.movementService = new MovementService(collisionDetector);
        this.iceManager = new IceManager();
        this.scoreService = new ScoreService();
        this.stateManager = new GameStateManager(this);

        // Inicializar estado
        this.isPaused = false;
        this.currentLevelNumber = 1;
        this.lastUpdateTime = System.currentTimeMillis();

        // Comenzar en el estado de menú
        stateManager.changeState(new MenuState());
    }

    /**
     * Bucle principal de actualización del juego.
     */
    public void update() {
        long currentTime = System.currentTimeMillis();

        // Actualizar el estado actual
        stateManager.update();

        lastUpdateTime = currentTime;
    }

    /**
     * Actualiza todas las entidades del mapa.
     */

    /**
     * Actualiza todas las entidades del mapa.
     */
    public void updateEntities() {
        if (currentMap == null || isPaused) {
            return;
        }

        // Actualizar enemigos y su movimiento
        for (Enemy enemy : currentMap.getEnemies()) {
            if (!enemy.isActive())
                continue;

            MovementPattern pattern = enemy.getMovementPattern();
            if (pattern != null) {
                Direction nextDirection = pattern.calculateNextDirection(enemy, currentMap);
                movementService.moveEntity(enemy, nextDirection, currentMap);
            }
        }

        // Detectar colisiones del jugador
        Player player = currentMap.getPlayer();
        if (player != null && player.isActive()) {
            List<GameEntity> collisions = collisionDetector.detectPlayerCollisions(player, currentMap);

            for (GameEntity entity : collisions) {
                if (entity instanceof Fruit) {
                    Fruit fruit = (Fruit) entity;
                    if (!fruit.isCollected()) {
                        fruit.collect();
                        scoreService.addFruitScore(fruit.getPoints());
                    }
                } else if (entity instanceof Enemy) {
                    handlePlayerDeath();
                    break; // Solo morir una vez por frame
                }
            }
        }

        // Actualizar todas las entidades
        currentMap.updateAllEntities();
    }

    private void handlePlayerDeath() {
        Player player = currentMap.getPlayer();
        player.loseLife();

        if (player.getLives() > 0) {
            resetPositions();
        } else {
            changeState(new GameOverState());
        }
    }

    private void resetPositions() {
        // Resetear posición del jugador (asumiendo (1,1) como inicio por ahora)
        // Idealmente guardaríamos la posición inicial en el mapa o jugador
        if (currentMap != null && currentMap.getPlayer() != null) {
            currentMap.getPlayer().setPosition(new Position(1, 1));
        }

        // Resetear enemigos a sus posiciones iniciales si fuera necesario
        // Por ahora solo reseteamos al jugador para dar una oportunidad
    }

    /**
     * Mueve al jugador en una dirección.
     */
    public boolean movePlayer(Direction direction) {
        if (currentMap == null || currentMap.getPlayer() == null) {
            return false;
        }

        return movementService.moveEntity(currentMap.getPlayer(), direction, currentMap);
    }

    /**
     * El jugador crea un bloque de hielo.
     */
    public boolean playerCreateIce(Direction direction) {
        if (currentMap == null || currentMap.getPlayer() == null) {
            return false;
        }

        Position playerPos = currentMap.getPlayer().getPosition();
        IceBlock ice = iceManager.createIceBlock(playerPos, direction, currentMap);
        return ice != null;
    }

    /**
     * El jugador destruye un bloque de hielo.
     */
    public boolean playerDestroyIce(Direction direction) {
        if (currentMap == null || currentMap.getPlayer() == null) {
            return false;
        }

        Position playerPos = currentMap.getPlayer().getPosition();
        return iceManager.destroyIceBlock(playerPos, direction, currentMap);
    }

    /**
     * Crea un nivel hardcodeado (sin archivos JSON).
     */
    private void createLevel(int levelNumber) {
        currentMap = EntityFactory.createDefaultMap();

        // Crear jugador
        Player player = EntityFactory.createPlayer(1, 1);
        currentMap.setPlayer(player);

        // Crear paredes del borde
        for (int x = 0; x < 15; x++) {
            currentMap.addEntity(EntityFactory.createWall(x, 0));
            currentMap.addEntity(EntityFactory.createWall(x, 10));
        }
        for (int y = 1; y < 10; y++) {
            currentMap.addEntity(EntityFactory.createWall(0, y));
            currentMap.addEntity(EntityFactory.createWall(14, y));
        }

        // Añadir obstáculos internos
        currentMap.addEntity(EntityFactory.createWall(3, 3));
        currentMap.addEntity(EntityFactory.createWall(5, 5));
        currentMap.addEntity(EntityFactory.createWall(9, 5));
        currentMap.addEntity(EntityFactory.createWall(11, 3));

        // Añadir frutas
        currentMap.addEntity(EntityFactory.createFruit(7, 5, "apple", 100));
        currentMap.addEntity(EntityFactory.createFruit(3, 7, "cherry", 150));
        currentMap.addEntity(EntityFactory.createFruit(11, 7, "strawberry", 150));
        currentMap.addEntity(EntityFactory.createFruit(7, 2, "banana", 100));
        currentMap.addEntity(EntityFactory.createFruit(7, 8, "orange", 100));

        // Añadir enemigos
        currentMap.addEntity(EntityFactory.createEnemy(13, 9, "horizontal", "basic"));
        currentMap.addEntity(EntityFactory.createEnemy(1, 9, "vertical", "basic"));

        // Configurar score service
        scoreService.setTotalFruits(5);
        scoreService.setCurrentLevel(levelNumber);

        resetGameTimer();
    }

    /**
     * Carga el nivel actual por número.
     */
    public void loadCurrentLevel() {
        createLevel(currentLevelNumber);
    }

    /**
     * Avanza al siguiente nivel.
     */
    public void nextLevel() {
        currentLevelNumber++;
        // Solo hay 1 nivel hardcodeado, así que reinicia al nivel 1
        if (currentLevelNumber > 1) {
            currentLevelNumber = 1;
        }
        scoreService.nextLevel();
        scoreService.addLevelCompletionScore();
        loadCurrentLevel();
    }

    /**
     * Reinicia el nivel actual.
     */
    public void restartLevel() {
        loadCurrentLevel();
    }

    /**
     * Inicia un nuevo juego.
     */
    public void startNewGame() {
        currentLevelNumber = 1;
        scoreService.resetCurrentScore();
        loadCurrentLevel();
    }

    /**
     * Verifica si se cumple la condición de victoria.
     */
    public boolean checkVictoryCondition() {
        return scoreService.areAllFruitsCollected();
    }

    /**
     * Verifica si se cumple la condición de derrota.
     */
    public boolean checkDefeatCondition() {
        Player player = currentMap != null ? currentMap.getPlayer() : null;
        return player == null || !player.isActive() || player.getLives() <= 0;
    }

    /**
     * Cambia el estado del juego.
     */
    public void changeState(GameState newState) {
        stateManager.changeState(newState);
    }

    // Control de tiempo
    public void resetGameTimer() {
        this.gameStartTime = System.currentTimeMillis();
        this.pausedTime = 0;
    }

    public void pauseGameTimer() {
        if (!isPaused) {
            this.pausedTime = System.currentTimeMillis();
            this.isPaused = true;
        }
    }

    public void resumeGameTimer() {
        if (isPaused) {
            long pauseDuration = System.currentTimeMillis() - pausedTime;
            this.gameStartTime += pauseDuration;
            this.isPaused = false;
        }
    }

    public long getElapsedTime() {
        if (isPaused) {
            return pausedTime - gameStartTime;
        }
        return System.currentTimeMillis() - gameStartTime;
    }

    // Getters
    public GameMap getCurrentMap() {
        return currentMap;
    }

    public Player getPlayer() {
        return currentMap != null ? currentMap.getPlayer() : null;
    }

    public ScoreService getScoreService() {
        return scoreService;
    }

    public GameStateManager getStateManager() {
        return stateManager;
    }

    public boolean isPaused() {
        return isPaused;
    }

    public int getCurrentLevelNumber() {
        return currentLevelNumber;
    }

    public MovementService getMovementService() {
        return movementService;
    }

    public CollisionDetector getCollisionDetector() {
        return collisionDetector;
    }

    public IceManager getIceManager() {
        return iceManager;
    }
}
