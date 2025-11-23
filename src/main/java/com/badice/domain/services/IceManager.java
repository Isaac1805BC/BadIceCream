package com.badice.domain.services;

import com.badice.domain.entities.Direction;
import com.badice.domain.entities.GameMap;
import com.badice.domain.entities.IceBlock;
import com.badice.domain.entities.Position;

/**
 * Servicio que maneja la lógica de creación y destrucción de hielo.
 */
public class IceManager {

    /**
     * Crea un bloque de hielo en la dirección especificada desde una posición.
     * 
     * @return El bloque de hielo creado, o null si no se pudo crear
     */
    /**
     * Crea una fila de bloques de hielo en la dirección especificada desde una
     * posición.
     * La fila se extiende hasta encontrar un obstáculo o el borde del mapa.
     * 
     * @return El último bloque de hielo creado, o null si no se creó ninguno
     */
    public IceBlock createIceBlock(Position startPosition, Direction direction, GameMap map) {
        Position currentPos = startPosition.move(direction);
        IceBlock lastCreated = null;

        while (map.isValidPosition(currentPos)) {
            // Si encontramos un obstáculo (algo sólido que no sea hielo inactivo),
            // detenemos la creación
            if (map.isPositionBlocked(currentPos)) {
                break;
            }

            // Crear el bloque de hielo si no existe uno ya activo
            if (!hasIceBlockAt(currentPos, map)) {
                IceBlock iceBlock = new IceBlock(currentPos);
                map.addIceBlock(currentPos);
                lastCreated = iceBlock;
            }

            // Avanzar a la siguiente posición
            currentPos = currentPos.move(direction);
        }

        return lastCreated;
    }

    /**
     * Destruye una fila de bloques de hielo en la dirección especificada.
     * Destruye bloques contiguos hasta encontrar un espacio vacío o un obstáculo.
     * 
     * @return true si se destruyó al menos un bloque, false si no
     */
    public boolean destroyIceBlock(Position startPosition, Direction direction, GameMap map) {
        Position currentPos = startPosition.move(direction);
        boolean destroyedAny = false;

        while (map.isValidPosition(currentPos)) {
            IceBlock iceBlock = getIceBlockAt(currentPos, map);

            if (iceBlock != null) {
                iceBlock.destroy();
                map.removeEntity(iceBlock);
                destroyedAny = true;
            } else {
                // Si encontramos un espacio sin hielo (vacío o con otra entidad), dejamos de
                // destruir
                break;
            }

            // Avanzar a la siguiente posición
            currentPos = currentPos.move(direction);
        }

        return destroyedAny;
    }

    /**
     * Verifica si hay un bloque de hielo en una posición.
     */
    public boolean hasIceBlockAt(Position position, GameMap map) {
        return map.getIceBlocks().stream()
                .filter(IceBlock::isActive)
                .anyMatch(iceBlock -> iceBlock.getPosition().equals(position));
    }

    /**
     * Obtiene el bloque de hielo en una posición específica.
     */
    public IceBlock getIceBlockAt(Position position, GameMap map) {
        return map.getIceBlocks().stream()
                .filter(IceBlock::isActive)
                .filter(iceBlock -> iceBlock.getPosition().equals(position))
                .findFirst()
                .orElse(null);
    }

    /**
     * Destruye todos los bloques de hielo del mapa.
     */
    public void clearAllIceBlocks(GameMap map) {
        for (IceBlock iceBlock : map.getIceBlocks()) {
            iceBlock.destroy();
        }
        map.cleanupInactiveEntities();
    }
}
