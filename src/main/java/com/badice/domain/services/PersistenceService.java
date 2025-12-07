package com.badice.domain.services;

import com.badice.domain.models.GameSaveData;

import java.io.*;

/**
 * Servicio encargado de guardar y cargar el estado del juego usando
 * Serialization.
 */
public class PersistenceService {

    private static final String SAVE_FILE_NAME = "savegame.dat";

    /**
     * Guarda el estado actual del juego en un archivo.
     * 
     * @param state El estado del juego a guardar
     * @throws IOException Si ocurre un error de escritura
     */
    public void saveGame(GameSaveData state) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(SAVE_FILE_NAME))) {
            oos.writeObject(state);
            System.out.println("Game saved successfully to " + SAVE_FILE_NAME);
        }
    }

    /**
     * Carga el estado del juego desde el archivo.
     * 
     * @return El estado cargado
     * @throws IOException            Si hay error de lectura
     * @throws ClassNotFoundException Si las clases guardadas no coinciden
     */
    public GameSaveData loadGame() throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(SAVE_FILE_NAME))) {
            GameSaveData state = (GameSaveData) ois.readObject();
            System.out.println("Game loaded successfully from " + SAVE_FILE_NAME);
            return state;
        }
    }

    /**
     * Verifica si existe un archivo de guardado.
     */
    public boolean hasSavedGame() {
        File file = new File(SAVE_FILE_NAME);
        return file.exists() && file.isFile();
    }
}
