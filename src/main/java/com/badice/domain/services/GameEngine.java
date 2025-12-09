package com.badice.domain.services;

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

        // Añadir obstáculos internos (Hielo destructible)
        currentMap.addEntity(EntityFactory.createIceBlock(3, 3));
        currentMap.addEntity(EntityFactory.createIceBlock(5, 5));
        currentMap.addEntity(EntityFactory.createIceBlock(9, 5));
        currentMap.addEntity(EntityFactory.createIceBlock(11, 3));

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

            // Añadir enemigos Troll (nivel 1: movimiento horizontal)
            currentMap.addEntity(EntityFactory.createTrollEnemy(13, 9));
            currentMap.addEntity(EntityFactory.createTrollEnemy(1, 9));
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

            // Añadir enemigo Maceta (nivel 2: persigue al jugador)
            currentMap.addEntity(EntityFactory.createPotEnemy(13, 9));
            currentMap.addEntity(EntityFactory.createPotEnemy(1, 9));
        } else if (levelNumber == 3) {
            // Nivel 3: Cactus (peligrosos) y Piñas + FOGATAS
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

            // NUEVO: Añadir FOGATAS en posiciones estratégicas
            currentMap.addEntity(EntityFactory.createCampfire(6, 3));
            currentMap.addEntity(EntityFactory.createCampfire(8, 3));
            currentMap.addEntity(EntityFactory.createCampfire(7, 7));

            // Añadir enemigos (mezcla: Maceta + Calamar)
            currentMap.addEntity(EntityFactory.createPotEnemy(13, 9));
            // Calamar rompe hielo
            currentMap.addEntity(EntityFactory.createSquidEnemy(1, 9, currentMap));
        } else if (levelNumber == 4) {
            // Nivel 4: NUEVO - Con BALDOSAS CALIENTES, NARVAL y 3 FASES
            currentPhase = 1;
            totalPhases = 3;

            // FASE 1: Plátanos (100 pts c/u) - NUEVAS POSICIONES sin bloques
            currentMap.addEntity(EntityFactory.createBasicFruit(1, 2, "platano", 100));
            currentMap.addEntity(EntityFactory.createBasicFruit(13, 2, "platano", 100));
            currentMap.addEntity(EntityFactory.createBasicFruit(7, 2, "platano", 100));

            scoreService.setTotalFruits(3);

            // NUEVO: Añadir BALDOSAS CALIENTES (no bloquean pero derriten hielo)
            currentMap.addEntity(EntityFactory.createHotTile(4, 5));
            currentMap.addEntity(EntityFactory.createHotTile(10, 5));
            currentMap.addEntity(EntityFactory.createHotTile(7, 6));
            currentMap.addEntity(EntityFactory.createHotTile(5, 8));
            currentMap.addEntity(EntityFactory.createHotTile(9, 8));

            // NUEVO: Añadir FOGATAS también
            currentMap.addEntity(EntityFactory.createCampfire(2, 5));
            currentMap.addEntity(EntityFactory.createCampfire(12, 5));

            // NUEVO: Enemigo NARVAL que embiste
            currentMap.addEntity(EntityFactory.createNarvalEnemy(7, 9));
            // Añadir también un Calamar para variedad
            currentMap.addEntity(EntityFactory.createSquidEnemy(13, 5, currentMap));
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
        } else if (currentLevelNumber == 4 && currentPhase == 2) {
            // Nivel 4, Fase 2: Uvas moradas (50 pts c/u)
            currentMap.addEntity(EntityFactory.createBasicFruit(2, 3, "uva", 50));
            currentMap.addEntity(EntityFactory.createBasicFruit(12, 3, "uva", 50));
            currentMap.addEntity(EntityFactory.createBasicFruit(5, 7, "uva", 50));
            currentMap.addEntity(EntityFactory.createBasicFruit(9, 7, "uva", 50));

            scoreService.nextPhase(4); // 4 uvas
        } else if (currentLevelNumber == 4 && currentPhase == 3) {
            // Nivel 4, Fase 3: Cerezas (teletransporte, 150 pts c/u)
            currentMap.addEntity(EntityFactory.createCherryFruit(4, 2, currentMap));
            currentMap.addEntity(EntityFactory.createCherryFruit(10, 2, currentMap));
            currentMap.addEntity(EntityFactory.createCherryFruit(7, 8, currentMap));

            scoreService.nextPhase(3); // 3 cerezas
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
