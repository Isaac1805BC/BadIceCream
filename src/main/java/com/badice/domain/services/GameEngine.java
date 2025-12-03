package com.badice.domain.services;

import com.badice.domain.entities.*;
import com.badice.domain.interfaces.MovementPattern;
import com.badice.domain.states.GameState;
import com.badice.domain.states.MenuState;
import com.badice.domain.factories.EntityFactory;
import com.badice.domain.states.GameOverState;
import com.badice.domain.states.GameOverState;
import com.badice.domain.enums.GameMode;
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

    // Sistema de fases
    private int currentPhase;
    private int totalPhases;

    // Modo de juego
    private GameMode currentMode;

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
        this.currentPhase = 1;
        this.lastUpdateTime = System.currentTimeMillis();
        this.currentPhase = 1;
        this.totalPhases = 1;
        this.currentMode = GameMode.ONE_PLAYER;

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
    private int tickCounter = 0;

    /**
     * Actualiza todas las entidades del mapa.
     */
    public void updateEntities() {
        if (currentMap == null || isPaused) {
            return;
        }

        tickCounter++;
        boolean shouldMoveEnemies = tickCounter % 3 == 0; // Mover enemigos cada 3 ticks (aprox 300ms)

        // Actualizar enemigos y su movimiento
        for (Enemy enemy : currentMap.getEnemies()) {
            if (!enemy.isActive())
                continue;

            // Solo mover si toca este tick
            if (shouldMoveEnemies) {
                MovementPattern pattern = enemy.getMovementPattern();
                if (pattern != null) {
                    Direction nextDirection = pattern.calculateNextDirection(enemy, currentMap);
                    
                    boolean moved = false;
                    
                    // Lógica especial para el Calamar (SquidEnemy)
                    if (enemy instanceof SquidEnemy) {
                        Position nextPos = enemy.getPosition().move(nextDirection);
                        // Verificar si hay hielo en la siguiente posición
                        if (iceManager.hasIceBlockAt(nextPos, currentMap)) {
                            // Romper SOLO el bloque de hielo en nextPos (no toda la línea)
                            IceBlock iceBlock = iceManager.getIceBlockAt(nextPos, currentMap);
                            if (iceBlock != null) {
                                iceBlock.destroy();
                                currentMap.removeEntity(iceBlock);
                                System.out.println("Squid broke ice at " + nextPos);
                            }
                            moved = true; // Consideramos que "actuó"
                        } else {
                            // Mover normal
                            moved = movementService.moveEntity(enemy, nextDirection, currentMap);
                        }
                    } else {
                        // Enemigos normales
                        moved = movementService.moveEntity(enemy, nextDirection, currentMap);
                    }
                }
            }
            
            // Verificar colisión directa con jugadores
            for (Player player : currentMap.getPlayers()) {
                if (player.isActive() && enemy.getPosition().equals(player.getPosition())) {
                    handlePlayerDeath(player);
                }
            }
        }

        // Actualizar IA para jugadores máquina
        updateAI();

        // Detectar colisiones de los jugadores
        List<Player> players = currentMap.getPlayers();
        for (Player player : players) {
            if (player != null && player.isActive()) {
                List<GameEntity> collisions = collisionDetector.detectPlayerCollisions(player, currentMap);

                for (GameEntity entity : collisions) {
                    if (entity instanceof Fruit) {
                        Fruit fruit = (Fruit) entity;
                        
                        // Verificar si es un cactus peligroso
                        if (fruit instanceof CactusFruit && ((CactusFruit) fruit).isDangerous()) {
                            handlePlayerDeath(player);
                            break;
                        }

                        if (!fruit.isCollected()) {
                            fruit.collect();
                            scoreService.addFruitScore(fruit.getPoints());
                        }
                    } else if (entity instanceof Enemy) {
                        handlePlayerDeath(player);
                        break; // Solo morir una vez por frame
                    }
                }
            }
        }

        // Verificar transición de fase
        if (currentPhase < totalPhases && scoreService.areAllFruitsCollected())

        {
            spawnNextPhase();
        }

        // Actualizar todas las entidades
        currentMap.updateAllEntities();
    }

    private void handlePlayerDeath(Player player) {
        System.out.println("¡JUGADOR MUERTO! Vidas antes: " + player.getLives());

        // Muerte instantánea - Game Over directo
        player.loseLife();
        player.setInactive();

        System.out.println("Vidas después: " + player.getLives() + ", Activo: " + player.isActive());

        // Si todos los jugadores están muertos, game over
        boolean allDead = true;
        for (Player p : currentMap.getPlayers()) {
            if (p.isActive()) {
                allDead = false;
                break;
            }
        }

        if (allDead) {
            System.out.println("Todos los jugadores muertos. Cambiando a Game Over...");
            changeState(new GameOverState());
        }
    }

    private void resetPositions() {
        // Resetear posición del jugador (asumiendo (1,1) como inicio por ahora)
        // Resetear posición del jugador (asumiendo (1,1) como inicio por ahora)
        if (currentMap != null) {
            List<Player> players = currentMap.getPlayers();
            if (!players.isEmpty()) {
                players.get(0).setPosition(new Position(1, 1));
                if (players.size() > 1) {
                    players.get(1).setPosition(new Position(13, 1));
                }
            }
        }
    }

    /**
     * Mueve al jugador en una dirección.
     */
    public boolean movePlayer(Direction direction, int playerIndex) {
        if (currentMap == null)
            return false;

        List<Player> players = currentMap.getPlayers();
        if (playerIndex < 0 || playerIndex >= players.size())
            return false;

        Player player = players.get(playerIndex);
        if (!player.isActive())
            return false;

        return movementService.moveEntity(player, direction, currentMap);
    }

    /**
     * El jugador crea un bloque de hielo.
     */
    public boolean playerCreateIce(Direction direction, int playerIndex) {
        if (currentMap == null)
            return false;

        List<Player> players = currentMap.getPlayers();
        if (playerIndex < 0 || playerIndex >= players.size())
            return false;

        Player player = players.get(playerIndex);
        if (!player.isActive())
            return false;

        Position playerPos = player.getPosition();
        IceBlock ice = iceManager.createIceBlock(playerPos, direction, currentMap);
        return ice != null;
    }

    /**
     * El jugador destruye un bloque de hielo.
     */
    public boolean playerDestroyIce(Direction direction, int playerIndex) {
        if (currentMap == null)
            return false;

        List<Player> players = currentMap.getPlayers();
        if (playerIndex < 0 || playerIndex >= players.size())
            return false;

        Player player = players.get(playerIndex);
        if (!player.isActive())
            return false;

        Position playerPos = player.getPosition();
        return iceManager.destroyIceBlock(playerPos, direction, currentMap);
    }

    /**
     * Crea un nivel hardcodeado (sin archivos JSON).
     */
    private void createLevel(int levelNumber) {
        currentMap = EntityFactory.createDefaultMap();

        // Crear jugadores según el modo
        if (currentMode == GameMode.ONE_PLAYER) {
            Player p1 = EntityFactory.createPlayer(1, 1);
            currentMap.addPlayer(p1);
        } else if (currentMode == GameMode.PVP || currentMode == GameMode.MVM || currentMode == GameMode.PVM) {
            Player p1 = EntityFactory.createPlayer(1, 1);
            currentMap.addPlayer(p1);

            Player p2 = EntityFactory.createPlayer(13, 1); // P2 en la esquina opuesta
            currentMap.addPlayer(p2);
        }

        // Configurar IA si es necesario (PVM o MVM)
        // La IA se maneja en updateAI()

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

        // Configurar fases y frutas según nivel
        if (levelNumber == 1) {
            // Nivel 1: Fase 1 (Plátanos) → Fase 2 (Uvas)
            currentPhase = 1;
            totalPhases = 2;

            // Fase 1: Plátanos amarillos (100 puntos c/u)
            // Posiciones que NO coinciden con bloques
            currentMap.addEntity(EntityFactory.createBasicFruit(2, 2, "platano", 100));
            currentMap.addEntity(EntityFactory.createBasicFruit(12, 2, "platano", 100));
            currentMap.addEntity(EntityFactory.createBasicFruit(7, 3, "platano", 100));

            // Configurar score service - cuenta solo plátanos inicialmente
            scoreService.setTotalFruits(3);

            // Añadir enemigos básicos
            currentMap.addEntity(EntityFactory.createEnemy(13, 9, "horizontal", "basic"));
            currentMap.addEntity(EntityFactory.createEnemy(1, 9, "vertical", "basic"));
        } else if (levelNumber == 2) {
            // Nivel 2: Cerezas (teleport) y Piñas (mirror movement)
            currentPhase = 1;
            totalPhases = 1;

            // Obtener el jugador para las piñas
            Player player = currentMap.getPlayer();

            // Añadir cerezas (se teletransportan cada 20 segundos, 150 puntos c/u)
            currentMap.addEntity(EntityFactory.createCherryFruit(2, 2, currentMap));
            currentMap.addEntity(EntityFactory.createCherryFruit(12, 2, currentMap));
            currentMap.addEntity(EntityFactory.createCherryFruit(7, 5, currentMap));

            // Añadir piñas (se mueven cuando el jugador se mueve, 200 puntos c/u)
            if (player != null) {
                currentMap.addEntity(EntityFactory.createPineappleFruit(4, 4, player, currentMap));
                currentMap.addEntity(EntityFactory.createPineappleFruit(10, 4, player, currentMap));
            }

            // Total: 3 cerezas + 2 piñas = 5 frutas
            scoreService.setTotalFruits(5);

            // Añadir enemigo maceta (persigue al jugador, no puede romper hielo)
            currentMap.addEntity(EntityFactory.createPotEnemy(13, 9));
            currentMap.addEntity(EntityFactory.createPotEnemy(1, 9));
        } else if (levelNumber == 3) {
            // Nivel 3: Cactus (peligrosos) y Piñas
            currentPhase = 1;
            totalPhases = 1;

            Player player = currentMap.getPlayer();

            // Añadir Cactus (250 puntos c/u)
            currentMap.addEntity(EntityFactory.createCactusFruit(3, 4));
            currentMap.addEntity(EntityFactory.createCactusFruit(11, 4));
            currentMap.addEntity(EntityFactory.createCactusFruit(7, 5));

            // Añadir Piñas (200 puntos c/u)
            if (player != null) {
                currentMap.addEntity(EntityFactory.createPineappleFruit(2, 8, player, currentMap));
                currentMap.addEntity(EntityFactory.createPineappleFruit(12, 8, player, currentMap));
            }

            // Total: 3 cactus + 2 piñas = 5 frutas
            scoreService.setTotalFruits(5);

            // Añadir enemigos (mezcla)
            currentMap.addEntity(EntityFactory.createPotEnemy(13, 9));
            // Reemplazar enemigo básico con Calamar
            currentMap.addEntity(EntityFactory.createSquidEnemy(1, 9, currentMap));
        } else {
            // Otros niveles: comportamiento por defecto
            currentPhase = 1;
            totalPhases = 1;
            currentMap.addEntity(EntityFactory.createBasicFruit(7, 5, "apple", 100));
            scoreService.setTotalFruits(1);

            // Añadir enemigos
            currentMap.addEntity(EntityFactory.createEnemy(13, 9, "horizontal", "basic"));
            currentMap.addEntity(EntityFactory.createEnemy(1, 9, "vertical", "basic"));
        }

        scoreService.setCurrentLevel(levelNumber);

        resetGameTimer();
    }

    /**
     * Spawns la siguiente fase de frutas.
     */
    private void spawnNextPhase() {
        currentPhase++;

        if (currentLevelNumber == 1 && currentPhase == 2) {
            // Nivel 1, Fase 2: Spawner uvas moradas
            // Posiciones que NO coinciden con bloques
            currentMap.addEntity(EntityFactory.createBasicFruit(2, 7, "uva", 50));
            currentMap.addEntity(EntityFactory.createBasicFruit(12, 7, "uva", 50));
            currentMap.addEntity(EntityFactory.createBasicFruit(7, 7, "uva", 50));
            currentMap.addEntity(EntityFactory.createBasicFruit(4, 4, "uva", 50));
            currentMap.addEntity(EntityFactory.createBasicFruit(10, 4, "uva", 50));

            // Actualizar score service para contar uvas
            scoreService.nextPhase(5); // 5 uvas
        }
    }

    /**
     * Verifica si se cumple la condición de victoria.
     */
    public boolean checkVictoryCondition() {
        // Victoria solo si estamos en la última fase y todas las frutas están
        // recolectadas
        return currentPhase >= totalPhases && scoreService.areAllFruitsCollected();
    }

    /**
     * Verifica si se cumple la condición de derrota.
     */
    public boolean checkDefeatCondition() {
        if (currentMap == null)
            return true;

        boolean allDead = true;
        for (Player p : currentMap.getPlayers()) {
            if (p.isActive() && p.getLives() > 0) {
                allDead = false;
                break;
            }
        }
        return allDead;
    }

    /**
     * Carga el nivel actual por número.
     */
    public void loadCurrentLevel() {
        tickCounter = 0; // Reset tick counter para asegurar que enemigos se muevan correctamente
        createLevel(currentLevelNumber);
    }

    /**
     * Avanza al siguiente nivel.
     */
    public void nextLevel() {
        currentLevelNumber++;
        // Ahora soportamos 3 niveles, reiniciar al nivel 1 después del nivel 3
        if (currentLevelNumber > 3) {
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
    /**
     * Inicia un nuevo juego.
     */
    public void startNewGame(GameMode mode) {
        startLevel(1, mode);
    }

    /**
     * Inicia el juego en un nivel específico.
     */
    public void startLevel(int levelNumber, GameMode mode) {
        this.currentMode = mode;
        this.currentLevelNumber = levelNumber;
        scoreService.resetCurrentScore();
        loadCurrentLevel();
    }

    /**
     * Inicia un nuevo juego con colores de jugador especificados.
     */
    public void startNewGameWithColors(GameMode mode, String player1Color, String player2Color) {
        this.currentMode = mode;
        this.currentLevelNumber = 1;
        scoreService.resetCurrentScore();
        createLevelWithColors(currentLevelNumber, player1Color, player2Color);
        resetGameTimer();
    }

    /**
     * Crea un nivel con colores de jugador personalizados.
     */
    private void createLevelWithColors(int levelNumber, String player1Color, String player2Color) {
        // Crear el nivel normalmente
        createLevel(levelNumber);
        
        // Actualizar colores de los jugadores
        List<com.badice.domain.entities.Player> players = currentMap.getPlayers();
        if (!players.isEmpty() && player1Color != null) {
            players.get(0).setPlayerColor(player1Color);
        }
        if (players.size() > 1 && player2Color != null) {
            players.get(1).setPlayerColor(player2Color);
        }
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
        this.isPaused = false;
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

    public GameMode getCurrentMode() {
        return currentMode;
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

    private void updateAI() {
        if (currentMap == null)
            return;

        List<Player> players = currentMap.getPlayers();

        // Determinar qué jugadores son máquinas
        boolean p1IsMachine = (currentMode == GameMode.MVM);
        boolean p2IsMachine = (currentMode == GameMode.PVM || currentMode == GameMode.MVM);

        if (p1IsMachine && players.size() > 0) {
            moveMachinePlayer(players.get(0), 0);
        }

        if (p2IsMachine && players.size() > 1) {
            moveMachinePlayer(players.get(1), 1);
        }
    }

    private void moveMachinePlayer(Player player, int playerIndex) {
        if (!player.isActive())
            return;

        // Lógica simple: moverse aleatoriamente o hacia una fruta
        // Por ahora, movimiento aleatorio simple cada ciertos frames
        if (Math.random() < 0.05) { // 5% de probabilidad de cambio de dirección por frame
            Direction[] dirs = Direction.values();
            Direction randomDir = dirs[(int) (Math.random() * dirs.length)];
            movePlayer(randomDir, playerIndex);
        }

        // Intentar moverse en la dirección actual
        if (!movePlayer(player.getCurrentDirection(), playerIndex)) {
            // Si choca, cambiar dirección
            Direction[] dirs = Direction.values();
            Direction randomDir = dirs[(int) (Math.random() * dirs.length)];
            movePlayer(randomDir, playerIndex);
        }
    }
}
