package com.badice.presentation.controller;

import com.badice.domain.config.GameConfig;
import com.badice.domain.enums.GameMode;
import com.badice.domain.services.GameEngine;
import com.badice.domain.states.MenuState;
import com.badice.domain.states.PlayingState;
import com.badice.domain.enums.BotProfile;
import com.badice.domain.interfaces.BotStrategy;
import com.badice.domain.services.strategies.ExpertStrategy;
import com.badice.domain.services.strategies.FearfulStrategy;
import com.badice.domain.services.strategies.HungryStrategy;
import com.badice.presentation.view.*;

import javax.swing.*;

/**
 * Controlador principal que conecta la vista con el dominio.
 */
public class GameController {
    private final GameEngine gameEngine;
    private final MainFrame mainFrame;
    private final GamePanel gamePanel;
    private final MenuPanel menuPanel;
    private final GameOverPanel gameOverPanel;
    private final GameModeSelectionPanel gameModeSelectionPanel;
    private final LevelSelectionPanel levelSelectionPanel;
    private final PlayerColorSelectionPanel playerColorSelectionPanel;
    private final BotProfileSelectionPanel botProfileSelectionPanel;
    private final VictoryPanel victoryPanel;

    // Variables para el flujo de selección de color
    private GameMode selectedGameMode;
    private String player1Color;
    private String player2Color;
    private int selectedLevel = 1;

    private final InputHandler inputHandler;
    private final ActionMapper actionMapper;
    private final com.badice.domain.services.PersistenceService persistenceService;

    private Timer gameLoopTimer;
    private Timer renderTimer;

    public GameController() {
        this.gameEngine = new GameEngine();
        this.actionMapper = new ActionMapper(gameEngine);
        this.inputHandler = new InputHandler(actionMapper);
        this.persistenceService = new com.badice.domain.services.PersistenceService();

        // Crear vistas
        this.gamePanel = new GamePanel(gameEngine);
        this.menuPanel = new MenuPanel();
        this.gameOverPanel = new GameOverPanel();
        this.gameModeSelectionPanel = new GameModeSelectionPanel();
        this.levelSelectionPanel = new LevelSelectionPanel();
        this.playerColorSelectionPanel = new PlayerColorSelectionPanel();
        // Inicializar con modo por defecto, se actualizará al mostrar
        this.botProfileSelectionPanel = new BotProfileSelectionPanel(GameMode.PVM);
        this.victoryPanel = new VictoryPanel();
        this.mainFrame = new MainFrame();

        setupEventHandlers();
        setupGameLoop();

        // Iniciar en el menú
        showMenu();
    }

    /**
     * Configura los manejadores de eventos de la UI.
     */
    private void setupEventHandlers() {
        // Menu panel listeners
        menuPanel.setPlayButtonListener(e -> showGameModeSelection());
        menuPanel.setLoadGameButtonListener(e -> loadGame());
        menuPanel.setSelectLevelButtonListener(e -> showLevelSelection());
        menuPanel.setExitButtonListener(e -> System.exit(0));

        // Game mode selection panel listeners
        gameModeSelectionPanel.setOnePlayerButtonListener(e -> handleGameModeSelection(GameMode.ONE_PLAYER));
        gameModeSelectionPanel.setPvpButtonListener(e -> handleGameModeSelection(GameMode.PVP));
        gameModeSelectionPanel.setPvmButtonListener(e -> handleGameModeSelection(GameMode.PVM));
        gameModeSelectionPanel.setMvmButtonListener(e -> handleGameModeSelection(GameMode.MVM));
        gameModeSelectionPanel.setBackButtonListener(e -> showMenu());

        // Level selection panel listeners
        levelSelectionPanel.setLevel1ButtonListener(e -> showGameModeSelectionForLevel(1));
        levelSelectionPanel.setLevel2ButtonListener(e -> showGameModeSelectionForLevel(2));
        levelSelectionPanel.setLevel3ButtonListener(e -> showGameModeSelectionForLevel(3));
        levelSelectionPanel.setLevel4ButtonListener(e -> showGameModeSelectionForLevel(4));
        levelSelectionPanel.setBackButtonListener(e -> showMenu());

        // Player color selection panel listeners
        playerColorSelectionPanel.setRedButtonListener(e -> handleColorSelection("red"));
        playerColorSelectionPanel.setBrownButtonListener(e -> handleColorSelection("brown"));
        playerColorSelectionPanel.setBlueButtonListener(e -> handleColorSelection("blue"));
        playerColorSelectionPanel.setBackButtonListener(e -> showGameModeSelection());

        // Bot profile selection panel listeners
        botProfileSelectionPanel.setNextButtonListener(e -> handleBotProfileSelection());
        botProfileSelectionPanel.setBackButtonListener(e -> showGameModeSelection());

        // Victory panel listeners
        victoryPanel.setNextLevelButtonListener(e -> {
            gameEngine.nextLevel();
            gameEngine.changeState(new PlayingState());
            showGamePanel();
            startGameLoop();
        });
        victoryPanel.setMainMenuButtonListener(e -> showMenu());

        // Game over panel listeners
        gameOverPanel.setRetryButtonListener(e -> retryLevel());
        gameOverPanel.setMainMenuButtonListener(e -> showMenu());

        // Input handler
        gamePanel.addKeyListener(inputHandler);

        // Save/Load key listener (F5/F9)
        gamePanel.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_F5) {
                    saveGame();
                } else if (e.getKeyCode() == java.awt.event.KeyEvent.VK_F9) {
                    loadGame();
                }
            }
        });
    }

    /**
     * Configura el bucle principal del juego.
     */
    private void setupGameLoop() {
        // Timer para lógica del juego (10 FPS)
        gameLoopTimer = new Timer(GameConfig.GAME_TICK_RATE, e -> {
            gameEngine.update();
            checkGameState();
        });

        // Timer para renderizado (60 FPS)
        renderTimer = new Timer(1000 / GameConfig.RENDER_FPS, e -> {
            gamePanel.update();
        });
    }

    /**
     * Inicia un nuevo juego.
     */
    public void startNewGame(GameMode mode) {
        gameEngine.startNewGame(mode);
        gameEngine.changeState(new PlayingState());
        showGamePanel();
        startGameLoop();
    }

    /**
     * Maneja la selecci\u00f3n de modo de juego.
     */
    private void handleGameModeSelection(GameMode mode) {
        selectedGameMode = mode;

        if (mode == GameMode.PVM || mode == GameMode.MVM) {
            // Mostrar selección de bots primero
            // Recrear panel con el modo correcto para mostrar los combos adecuados
            mainFrame.remove(botProfileSelectionPanel);
            BotProfileSelectionPanel newPanel = new BotProfileSelectionPanel(mode);
            newPanel.setNextButtonListener(e -> handleBotProfileSelection(newPanel));
            newPanel.setBackButtonListener(e -> showGameModeSelection());
            mainFrame.showPanel(newPanel);
        } else {
            // 1 Player o PvP: Directo a selección de color
            player1Color = null;
            player2Color = null;
            playerColorSelectionPanel.setPlayerNumber("1");
            playerColorSelectionPanel.setDisabledColor(null);
            mainFrame.showPanel(playerColorSelectionPanel);
        }
    }

    private void handleBotProfileSelection(BotProfileSelectionPanel panel) {
        // Configurar estrategias en el engine
        BotProfile p1Profile = panel.getBot1Profile();
        BotProfile p2Profile = panel.getBot2Profile();

        gameEngine.setBot1Strategy(createStrategyFromProfile(p1Profile));
        gameEngine.setBot2Strategy(createStrategyFromProfile(p2Profile));

        if (selectedGameMode == GameMode.MVM) {
            // MvM: Colores fijos, iniciar directo
            player1Color = "red";
            player2Color = "brown";
            startNewGameWithColors();
        } else {
            // PvM: El jugador humano elige color
            player1Color = null;
            player2Color = null;
            playerColorSelectionPanel.setPlayerNumber("1");
            playerColorSelectionPanel.setDisabledColor(null);
            mainFrame.showPanel(playerColorSelectionPanel);
        }
    }

    // Sobrecarga para mantener compatibilidad si es necesario, aunque usamos la
    // versión con argumento
    private void handleBotProfileSelection() {
        // No-op, usamos el listener dinámico
    }

    private BotStrategy createStrategyFromProfile(BotProfile profile) {
        switch (profile) {
            case HUNGRY:
                return new HungryStrategy();
            case FEARFUL:
                return new FearfulStrategy();
            case EXPERT:
                return new ExpertStrategy();
            default:
                return new HungryStrategy();
        }
    }

    /**
     * Maneja la selecci\u00f3n de color del jugador.
     */
    private void handleColorSelection(String color) {
        if (selectedGameMode == GameMode.ONE_PLAYER) {
            player1Color = color;
            startNewGameWithColors();
        }
        // Para PvP y PvM, se selecciona el color del jugador 1 y luego el del jugador 2
        else if (selectedGameMode == GameMode.PVP || selectedGameMode == GameMode.PVM) {
            if (player1Color == null) {
                // El primer jugador seleccion\u00f3 su color
                player1Color = color;
                // Preparar la selecci\u00f3n para el segundo jugador
                playerColorSelectionPanel.setPlayerNumber("2");
                playerColorSelectionPanel.setDisabledColor(color); // Deshabilitar el color ya elegido
                mainFrame.showPanel(playerColorSelectionPanel);
            } else {
                // El segundo jugador (o la IA en PvM) seleccion\u00f3 su color
                player2Color = color;
                startNewGameWithColors();
            }
        }
    }

    /**
     * Inicia el juego con los colores seleccionados.
     */
    private void startNewGameWithColors() {
        // Mostrar diálogo de username antes de iniciar
        boolean twoPlayers = (selectedGameMode == GameMode.PVP || selectedGameMode == GameMode.PVM);
        UsernameDialog usernameDialog = new UsernameDialog(mainFrame, twoPlayers);
        usernameDialog.setVisible(true);

        if (!usernameDialog.isConfirmed()) {
            // Usuario canceló, volver a selección de color
            player1Color = null;
            player2Color = null;
            showGameModeSelection();
            return;
        }

        // Iniciar juego con colores
        gameEngine.startNewGameWithColors(selectedGameMode, player1Color, player2Color);

        // Asignar usernames a los jugadores
        java.util.List<com.badice.domain.entities.Player> players = gameEngine.getCurrentMap().getPlayers();
        if (!players.isEmpty()) {
            players.get(0).setUsername(usernameDialog.getPlayer1Username());
        }
        if (players.size() > 1 && twoPlayers) {
            players.get(1).setUsername(usernameDialog.getPlayer2Username());
        }

        gameEngine.changeState(new PlayingState());
        showGamePanel();
        startGameLoop();
    }

    private void showGameModeSelection() {
        mainFrame.showPanel(gameModeSelectionPanel);
    }

    /**
     * Reintenta el nivel actual.
     */
    private void retryLevel() {
        stopGameLoop(); // Detener timers existentes primero

        // Guardar colores actuales de los jugadores
        java.util.List<String> playerColors = new java.util.ArrayList<>();
        if (gameEngine.getCurrentMap() != null) {
            for (com.badice.domain.entities.Player p : gameEngine.getCurrentMap().getPlayers()) {
                playerColors.add(p.getPlayerColor());
            }
        }

        gameEngine.restartLevel();

        // Restaurar colores
        if (gameEngine.getCurrentMap() != null && !playerColors.isEmpty()) {
            java.util.List<com.badice.domain.entities.Player> players = gameEngine.getCurrentMap().getPlayers();
            for (int i = 0; i < Math.min(players.size(), playerColors.size()); i++) {
                players.get(i).setPlayerColor(playerColors.get(i));
            }
        }

        gameEngine.changeState(new PlayingState());
        showGamePanel();
        startGameLoop();
    }

    /**
     * Muestra el menú principal.
     */
    public void showMenu() {
        stopGameLoop();
        gameEngine.changeState(new MenuState());
        // Reiniciar colores al volver al menú
        player1Color = null;
        player2Color = null;
        mainFrame.showPanel(menuPanel);
    }

    /**
     * Muestra el panel de juego.
     */
    private void showGamePanel() {
        mainFrame.showPanel(gamePanel);
        gamePanel.requestFocusInWindow();
    }

    /**
     * Muestra el panel de game over.
     */
    public void showGameOver() {
        stopGameLoop();
        gameOverPanel.setScore(gameEngine.getScoreService().getCurrentScore());
        mainFrame.showPanel(gameOverPanel);
    }

    /**
     * Muestra la selección de nivel.
     */
    private void showLevelSelection() {
        mainFrame.showPanel(levelSelectionPanel);
    }

    private void showGameModeSelectionForLevel(int level) {
        // Guardar el nivel seleccionado temporalmente
        final int selectedLevel = level;

        // Crear un panel temporal de selección de modo para este nivel
        GameModeSelectionPanel tempModePanel = new GameModeSelectionPanel();
        tempModePanel.setOnePlayerButtonListener(e -> {
            gameEngine.startLevel(selectedLevel, GameMode.ONE_PLAYER);
            gameEngine.changeState(new PlayingState());
            showGamePanel();
            startGameLoop();
        });
        tempModePanel.setPvpButtonListener(e -> {
            gameEngine.startLevel(selectedLevel, GameMode.PVP);
            gameEngine.changeState(new PlayingState());
            showGamePanel();
            startGameLoop();
        });
        tempModePanel.setPvmButtonListener(e -> {
            gameEngine.startLevel(selectedLevel, GameMode.PVM);
            gameEngine.changeState(new PlayingState());
            showGamePanel();
            startGameLoop();
        });
        tempModePanel.setMvmButtonListener(e -> {
            gameEngine.startLevel(selectedLevel, GameMode.MVM);
            gameEngine.changeState(new PlayingState());
            showGamePanel();
            startGameLoop();
        });
        tempModePanel.setBackButtonListener(e -> showLevelSelection());

        mainFrame.showPanel(tempModePanel);
    }

    private void checkGameState() {
        if (gameEngine.getStateManager().isInState(com.badice.domain.states.LevelCompleteState.class)) {
            stopGameLoop();
            showVictoryScreen();
        } else if (gameEngine.getStateManager().isInState(com.badice.domain.states.GameOverState.class)) {
            showGameOver();
        }
    }

    private void showVictoryScreen() {
        // Determinar ganador por puntos
        java.util.List<com.badice.domain.entities.Player> players = gameEngine.getCurrentMap().getPlayers();

        // Obtener el score actual del servicio de puntuación
        int currentScore = gameEngine.getScoreService().getCurrentScore();

        if (gameEngine.getCurrentMode() == com.badice.domain.enums.GameMode.ONE_PLAYER) {
            // Modo un jugador: mostrar el jugador con su puntuación del ScoreService
            if (!players.isEmpty()) {
                victoryPanel.setWinner(players.get(0).getPlayerColor(), currentScore);
                victoryPanel.setSinglePlayerMode(true);
            }
        } else {
            // Modos multijugador: determinar ganador
            victoryPanel.setSinglePlayerMode(false);
            if (players.size() >= 2) {
                com.badice.domain.entities.Player winner = players.get(0).getScore() > players.get(1).getScore()
                        ? players.get(0)
                        : players.get(1);
                victoryPanel.setWinner(winner.getPlayerColor(), currentScore);
            } else if (!players.isEmpty()) {
                victoryPanel.setWinner(players.get(0).getPlayerColor(), currentScore);
            }
        }
        mainFrame.showPanel(victoryPanel);
    }

    private void showLevelCompleteDialog() {
        Object[] options = { "Siguiente Nivel", "Menú Principal" };
        int n = JOptionPane.showOptionDialog(mainFrame,
                "¡Nivel Completado!\n¿Qué deseas hacer?",
                "Nivel Completado",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);

        if (n == JOptionPane.YES_OPTION) {
            // Siguiente Nivel
            gameEngine.nextLevel();
            gameEngine.changeState(new PlayingState());
            startGameLoop();
        } else {
            // Menú Principal
            showMenu();
        }
    }

    /**
     * Inicia el bucle del juego.
     */
    private void startGameLoop() {
        gameLoopTimer.start();
        renderTimer.start();
    }

    /**
     * Detiene el bucle del juego.
     */
    private void stopGameLoop() {
        gameLoopTimer.stop();
        renderTimer.stop();
    }

    /**
     * Guarda el juego actual.
     */
    private void saveGame() {
        if (!gameEngine.getStateManager().isInState(PlayingState.class) && !gameEngine.isPaused()) {
            JOptionPane.showMessageDialog(mainFrame, "Solo puedes guardar durante el juego.", "Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            com.badice.domain.models.GameSaveData data = gameEngine.getGameState();
            persistenceService.saveGame(data);
            JOptionPane.showMessageDialog(mainFrame, "¡Juego guardado correctamente!", "Guardado",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(mainFrame, "Error al guardar el juego: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Carga el juego guardado.
     */
    private void loadGame() {
        if (!persistenceService.hasSavedGame()) {
            JOptionPane.showMessageDialog(mainFrame, "No hay partida guardada.", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            com.badice.domain.models.GameSaveData data = persistenceService.loadGame();

            // Pausar loop antes de restaurar
            stopGameLoop();

            // Restaurar estado
            gameEngine.restoreGameState(data);
            gameEngine.changeState(new PlayingState());

            // Re-vincular UI si es necesario y refrescar
            showGamePanel();

            // Reiniciar loop
            startGameLoop();

            JOptionPane.showMessageDialog(mainFrame, "¡Juego cargado correctamente!", "Cargado",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(mainFrame, "Error al cargar el juego: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Obtiene el frame principal.
     */
    public MainFrame getMainFrame() {
        return mainFrame;
    }

    /**
     * Inicia la aplicación.
     */
    public void start() {
        mainFrame.setVisible(true);
    }
}
