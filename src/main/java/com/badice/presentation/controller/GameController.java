package com.badice.presentation.controller;

import com.badice.domain.config.GameConfig;
import com.badice.domain.enums.GameMode;
import com.badice.domain.services.GameEngine;
import com.badice.domain.states.MenuState;
import com.badice.domain.states.PlayingState;
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

    private final InputHandler inputHandler;
    private final ActionMapper actionMapper;

    private Timer gameLoopTimer;
    private Timer renderTimer;

    public GameController() {
        this.gameEngine = new GameEngine();
        this.actionMapper = new ActionMapper(gameEngine);
        this.inputHandler = new InputHandler(actionMapper);

        // Crear vistas
        this.gamePanel = new GamePanel(gameEngine);
        this.menuPanel = new MenuPanel();
        this.gameOverPanel = new GameOverPanel();
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
        menuPanel.setSelectLevelButtonListener(e -> showLevelSelection());
        menuPanel.setExitButtonListener(e -> System.exit(0));

        // Game over panel listeners
        gameOverPanel.setRetryButtonListener(e -> retryLevel());
        gameOverPanel.setMainMenuButtonListener(e -> showMenu());

        // Input handler
        gamePanel.addKeyListener(inputHandler);
    }

    /**
     * Configura el bucle principal del juego.
     */
    private void setupGameLoop() {
        // Timer para lógica del juego (10 FPS)
        gameLoopTimer = new Timer(GameConfig.GAME_TICK_RATE, e -> {
            gameEngine.update();
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

    private void showGameModeSelection() {
        String[] options = { "1 Player", "Player vs Player", "Player vs Machine", "Machine vs Machine" };
        int choice = JOptionPane.showOptionDialog(mainFrame,
                "Selecciona el modo de juego:",
                "Modo de Juego",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);

        if (choice >= 0) {
            GameMode mode = switch (choice) {
                case 0 -> GameMode.ONE_PLAYER;
                case 1 -> GameMode.PVP;
                case 2 -> GameMode.PVM;
                case 3 -> GameMode.MVM;
                default -> GameMode.ONE_PLAYER;
            };
            startNewGame(mode);
        }
    }

    /**
     * Reintenta el nivel actual.
     */
    private void retryLevel() {
        gameEngine.restartLevel();
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
     * Muestra la selección de nivel (por implementar).
     */
    private void showLevelSelection() {
        JOptionPane.showMessageDialog(mainFrame,
                "Selección de nivel no implementada aún",
                "Info",
                JOptionPane.INFORMATION_MESSAGE);
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
