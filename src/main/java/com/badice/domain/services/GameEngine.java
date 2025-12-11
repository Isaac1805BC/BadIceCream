package com.badice.domain.services;

import com.badice.domain.config.LevelConfig.EntityConfig;
import com.badice.domain.config.LevelConfig.PhaseConfig;
import com.badice.domain.entities.*;
import com.badice.domain.interfaces.MovementPattern;
import com.badice.domain.models.GameSaveData;
import com.badice.domain.states.GameState;
import com.badice.domain.states.MenuState;
import com.badice.domain.factories.EntityFactory;
import com.badice.domain.states.GameOverState;
import com.badice.domain.states.LevelCompleteState;
import com.badice.domain.enums.GameMode;
import com.badice.domain.interfaces.BotStrategy;
import com.badice.domain.services.strategies.HungryStrategy;
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
    private final LevelLoader levelLoader;

    // Estado del juego
    private GameMap currentMap;
    private long gameStartTime;
    private long pausedTime;
    private boolean isPaused;
    private int currentLevelNumber;
    private com.badice.domain.config.LevelConfig currentLevelConfig;

    // Sistema de fases
    private int currentPhase;
    private int totalPhases;

    // Modo de juego
    private GameMode currentMode;
    private BotStrategy bot1Strategy;
    private BotStrategy bot2Strategy;

    // Control de tiempo
    private static final long LEVEL_TIME_LIMIT = 3 * 60 * 1000; // 3 minutos en milisegundos

    // Contador de ticks para actualizaciones no críticas
    private int tickCounter = 0;

    public GameEngine() {
        // Inicializar servicios
        this.collisionDetector = new CollisionDetector();
        this.movementService = new MovementService(collisionDetector);
        this.iceManager = new IceManager();
        this.scoreService = new ScoreService();
        this.stateManager = new GameStateManager(this);
        this.levelLoader = new LevelLoader();

        // Inicializar estado
        this.isPaused = false;
        this.currentLevelNumber = 1;
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
        // Actualizar el estado actual
        stateManager.update();

        // Verificar tiempo límite
        if (getElapsedTime() >= LEVEL_TIME_LIMIT) {
            System.out.println("¡TIEMPO AGOTADO! Game Over.");
            changeState(new GameOverState());
        }
    }

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

                    // NUEVO: Lógica especial para el Narval (NarvalEnemy)
                    if (enemy instanceof NarvalEnemy) {
                        NarvalEnemy narval = (NarvalEnemy) enemy;

                        // Verificar alineación con jugadores
                        for (Player player : currentMap.getPlayers()) {
                            if (player.isActive() && narval.isAlignedWithPlayer(player)) {
                                narval.startCharge();
                                break;
                            }
                        }

                        // Si está cargando, moverse en dirección de embestida y destruir hielo
                        if (narval.isCharging()) {
                            Position nextPos = narval.getPosition().move(narval.getChargeDirection());

                            // Destruir hielo durante embestida
                            if (iceManager.hasIceBlockAt(nextPos, currentMap)) {
                                IceBlock iceBlock = iceManager.getIceBlockAt(nextPos, currentMap);
                                if (iceBlock != null) {
                                    iceBlock.destroy();
                                    currentMap.removeEntity(iceBlock);
                                }
                            }

                            // Moverse en dirección de embestida
                            movementService.moveEntity(narval, narval.getChargeDirection(), currentMap);
                        } else {
                            // Movimiento normal - usar patrón de movimiento
                            movementService.moveEntity(narval, nextDirection, currentMap);
                        }
                    }
                    // Lógica especial para el Calamar (SquidEnemy)
                    else if (enemy instanceof SquidEnemy) {
                        Position nextPos = enemy.getPosition().move(nextDirection);

                        // Si hay hielo en la siguiente posición, romperlo
                        if (iceManager.hasIceBlockAt(nextPos, currentMap)) {
                            IceBlock iceBlock = iceManager.getIceBlockAt(nextPos, currentMap);
                            if (iceBlock != null) {
                                iceBlock.destroy();
                                currentMap.removeEntity(iceBlock);
                            }
                        }

                        // Intentar mover después de romper hielo
                        movementService.moveEntity(enemy, nextDirection, currentMap);
                    } else {
                        // Enemigos normales
                        movementService.moveEntity(enemy, nextDirection, currentMap);
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
                if (player != null && player.isActive()) {
                    // Usar el método centralizado que maneja frutas, enemigos Y FOGATAS
                    collisionDetector.handlePlayerCollisions(player, currentMap, scoreService);
                }
            }
        }

        // Verificar transición de fase
        if (currentPhase < totalPhases && scoreService.areAllFruitsCollected())

        {
            spawnNextPhase();
        } else if (checkVictoryCondition()) {
             System.out.println("¡NIVEL COMPLETADO!");
             changeState(new LevelCompleteState());
        }

        // Actualizar todas las entidades
        currentMap.updateAllEntities();
    }

    private void handlePlayerDeath(Player player) {

        // Muerte instantánea - Game Over directo
        player.loseLife();
        player.setInactive();

        // Resetear contador de frutas al morir - SOLO en modo 1 jugador
        if (currentMode == GameMode.ONE_PLAYER) {
             scoreService.setFruitsCollected(0);
        }

        // Si todos los jugadores están muertos, game over
        boolean allDead = true;
        for (Player p : currentMap.getPlayers()) {
            if (p.isActive()) {
                allDead = false;
                break;
            }
        }

        if (allDead) {
            changeState(new GameOverState());
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

        // IMPORTANTE: Actualizar dirección SIEMPRE, incluso si el movimiento falla
        player.setDirection(direction);

        // Intentar mover (puede fallar si está bloqueado)
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
     * Crea un nivel cargando la configuración desde archivo.
     */
    private void createLevel(int levelNumber) {
        try {
            this.currentLevelConfig = levelLoader.loadLevel(levelNumber);
        } catch (com.badice.domain.exceptions.LevelLoadException e) {
            System.err.println("Error cargando nivel " + levelNumber + ": " + e.getMessage());
            e.printStackTrace();
            currentMap = EntityFactory.createDefaultMap();
            return;
        }

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

        // Crear paredes del borde
        for (int x = 0; x < 15; x++) {
            currentMap.addEntity(EntityFactory.createWall(x, 0));
            currentMap.addEntity(EntityFactory.createWall(x, 10));
        }
        for (int y = 1; y < 10; y++) {
            currentMap.addEntity(EntityFactory.createWall(0, y));
            currentMap.addEntity(EntityFactory.createWall(14, y));
        }

        // Configurar fases
        this.totalPhases = currentLevelConfig.getTotalPhases();
        this.currentPhase = 1;

        // Cargar entidades de la fase 1
        if (!currentLevelConfig.getPhases().isEmpty()) {
            setupPhase(currentLevelConfig.getPhases().get(0));
        }
        
        // Calcular frutas SOLO de la primera fase para iniciar
        int phase1Fruits = 0;
        if (!currentLevelConfig.getPhases().isEmpty()) {
            com.badice.domain.config.LevelConfig.PhaseConfig phase1 = currentLevelConfig.getPhases().get(0);
            for (com.badice.domain.config.LevelConfig.EntityConfig entity : phase1.getEntities()) {
                 if ("FRUIT".equals(entity.getType())) {
                     phase1Fruits++;
                 }
            }
        }
        scoreService.setTotalFruits(phase1Fruits);

        scoreService.setCurrentLevel(levelNumber);
        resetGameTimer();
    }

    private void setupPhase(PhaseConfig phase) {
        for (EntityConfig entity : phase.getEntities()) {
            int x = entity.getPosition().getX();
            int y = entity.getPosition().getY();
            
            switch (entity.getType()) {
                case "FRUIT":
                    createFruitEntity(entity, x, y);
                    break;
                case "ENEMY":
                    createEnemyEntity(entity, x, y);
                    break;
                case "BLOCK":
                    if ("ICE".equals(entity.getSubType())) {
                        currentMap.addEntity(EntityFactory.createIceBlock(x, y));
                    }
                    break;
                case "CAMPFIRE":
                    currentMap.addEntity(EntityFactory.createCampfire(x, y));
                    break;
                case "HOTTILE":
                    currentMap.addEntity(EntityFactory.createHotTile(x, y));
                    break;
            }
        }
    }

    private void createFruitEntity(com.badice.domain.config.LevelConfig.EntityConfig entity, int x, int y) {
        String subType = entity.getSubType().toLowerCase();
        int points = entity.getPoints();

        if (subType.contains("cherry") || subType.contains("cereza")) {
            currentMap.addEntity(EntityFactory.createCherryFruit(x, y, currentMap));
        } else if (subType.contains("pineapple") || subType.contains("piña")) {
            Player player = currentMap.getPlayer();
            if (player != null) {
                currentMap.addEntity(EntityFactory.createPineappleFruit(x, y, player, currentMap));
            }
        } else if (subType.contains("cactus")) {
            currentMap.addEntity(EntityFactory.createCactusFruit(x, y));
        } else {
            currentMap.addEntity(EntityFactory.createBasicFruit(x, y, subType, points));
        }
    }

    private void createEnemyEntity(com.badice.domain.config.LevelConfig.EntityConfig entity, int x, int y) {
        String subType = entity.getSubType().toUpperCase();
        
        if (subType.contains("TROLL")) {
            currentMap.addEntity(EntityFactory.createTrollEnemy(x, y));
        } else if (subType.contains("POT") || subType.contains("MACETA")) {
            currentMap.addEntity(EntityFactory.createPotEnemy(x, y));
        } else if (subType.contains("SQUID") || subType.contains("CALAMAR")) {
             currentMap.addEntity(EntityFactory.createSquidEnemy(x, y, currentMap));
        } else if (subType.contains("NARVAL")) {
            currentMap.addEntity(EntityFactory.createNarvalEnemy(x, y));
        } else {
             currentMap.addEntity(EntityFactory.createEnemy(x, y, "horizontal", "basic"));
        }
    }

    /**
     * Spawns la siguiente fase de frutas.
     */
    private void spawnNextPhase() {
        currentPhase++;
        
        if (currentLevelConfig != null && currentLevelConfig.getPhases().size() >= currentPhase) {
            PhaseConfig phaseConfig = currentLevelConfig.getPhases().get(currentPhase - 1);
            setupPhase(phaseConfig);
            
            int fruitsAdded = 0;
            for (EntityConfig e : phaseConfig.getEntities()) {
                if ("FRUIT".equals(e.getType())) fruitsAdded++;
            }
            scoreService.nextPhase(fruitsAdded);
            
        } else {
             System.out.println("Warning: Fase " + currentPhase + " solicitada pero no existe configuración.");
        }
    }

    /**
     * Verifica si se cumple la condición de victoria.
     */
    public boolean checkVictoryCondition() {
        return currentPhase >= totalPhases && scoreService.areAllFruitsCollected();
    }

    /**
     * Obtiene los niveles disponibles desde el cargador.
     */
    public java.util.List<Integer> getAvailableLevels() {
        return levelLoader.getAvailableLevels();
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
        // Guardar colores de los jugadores antes de cambiar de nivel
        java.util.List<String> playerColors = new java.util.ArrayList<>();
        if (currentMap != null) {
            for (Player p : currentMap.getPlayers()) {
                playerColors.add(p.getPlayerColor());
            }
        }

        currentLevelNumber++;
        // Ahora soportamos 4 niveles, reiniciar al nivel 1 después del nivel 4
        if (currentLevelNumber > 4) {
            currentLevelNumber = 1;
        }
        scoreService.nextLevel();
        // REMOVIDO: scoreService.addLevelCompletionScore(); - Los puntos se dan al
        // completar, no al cambiar de nivel
        loadCurrentLevel();

        // Restaurar colores de los jugadores después de cargar el nuevo nivel
        if (currentMap != null && !playerColors.isEmpty()) {
            java.util.List<Player> newPlayers = currentMap.getPlayers();
            for (int i = 0; i < Math.min(newPlayers.size(), playerColors.size()); i++) {
                newPlayers.get(i).setPlayerColor(playerColors.get(i));
            }
        }
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

    /**
     * Obtiene el estado actual del juego para guardarlo.
     */
    public GameSaveData getGameState() {
        Player player = currentMap != null ? currentMap.getPlayer() : null;
        int lives = player != null ? player.getLives() : 0;

        return new GameSaveData(
                currentMap,
                player,
                currentLevelNumber,
                scoreService.getCurrentScore(),
                lives,
                getElapsedTime() // Guardamos el tiempo transcurrido
        );
    }

    /**
     * Restaura el estado del juego desde un objeto guardado.
     */
    public void restoreGameState(GameSaveData state) {
        this.currentMap = state.getGameMap();
        this.currentLevelNumber = state.getCurrentLevel();

        // Restaurar puntuación
        scoreService.resetCurrentScore();
        scoreService.addScore(state.getScore());
        scoreService.setCurrentLevel(state.getCurrentLevel());

        // IMPORTANTE: Recontar frutas del mapa cargado
        if (currentMap != null) {
            int totalFruits = 0;
            int collectedFruits = 0;

            for (com.badice.domain.entities.GameEntity entity : currentMap.getEntities()) {
                if (entity instanceof com.badice.domain.entities.Fruit) {
                    totalFruits++;
                    com.badice.domain.entities.Fruit fruit = (com.badice.domain.entities.Fruit) entity;
                    if (fruit.isCollected()) {
                        collectedFruits++;
                    }
                }
            }

            scoreService.setTotalFruits(totalFruits);
            scoreService.setFruitsCollected(collectedFruits);
        }

        // Restaurar tiempo
        // Ajustamos el tiempo de inicio para que coincida con el tiempo guardado
        this.gameStartTime = System.currentTimeMillis() - state.getTimeRemaining();

        // Asegurar que el mapa tenga las referencias correctas si es necesario
        if (currentMap != null) {
            // Re-vincular collisionDetector al movementService si fuera necesario,
            // pero MovementService se crea fresco en GameEngine.
            // Lo importante es que las entidades en el mapa sean válidas.
        }

        // Forzar actualización de UI
        update();
    }

    public long getElapsedTime() {
        if (isPaused) {
            return pausedTime - gameStartTime;
        }
        return System.currentTimeMillis() - gameStartTime;
    }

    public long getTimeRemaining() {
        long elapsed = getElapsedTime();
        return Math.max(0, LEVEL_TIME_LIMIT - elapsed);
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

        BotStrategy strategy = null;
        // Asignar estrategia según índice (Bot 1 o Bot 2)
        // En MvM: P1 es bot1, P2 es bot2
        // En PvM: P2 es bot1 (el único bot)
        
        if (currentMode == GameMode.MVM) {
            strategy = (playerIndex == 0) ? bot1Strategy : bot2Strategy;
        } else if (currentMode == GameMode.PVM) {
            strategy = bot1Strategy; // Solo hay un bot
        }
        
        // Si no hay estrategia asignada, usar Hungry por defecto
        if (strategy == null) {
            strategy = new HungryStrategy();
        }
        
        // Ejecutar movimiento cada 3 ticks (aprox 300ms, más rápido que antes)
        if (tickCounter % 3 == 0) {
            Direction nextDir = strategy.calculateNextMove(player, currentMap);
            movePlayer(nextDir, playerIndex);
        }
    }

    public void setBot1Strategy(BotStrategy strategy) {
        this.bot1Strategy = strategy;
    }

    public void setBot2Strategy(BotStrategy strategy) {
        this.bot2Strategy = strategy;
    }
}
