package com.badice.domain.services;

import com.badice.domain.entities.*;

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

            // Verificar si hay una baldosa caliente en esta posición
            // Si la hay, el hielo se derrite instantáneamente (no se crea)
            if (hasHotTileAt(currentPos, map)) {
                // No crear hielo, pero continuar la línea
                currentPos = currentPos.move(direction);
                continue;
            }

            // Crear el bloque de hielo (incluso sobre fogatas)
            if (!hasIceBlockAt(currentPos, map)) {
                IceBlock iceBlock = new IceBlock(currentPos);
                map.addEntity(iceBlock); // Agregar directamente como entidad
                lastCreated = iceBlock;
            }

            // Avanzar a la siguiente posición
            currentPos = currentPos.move(direction);
        }

        return lastCreated;
    }

    /**
     * Destruye una fila de bloques de hielo en la dirección especificada.
     * Los bloques se agrietan en cascada (efecto dominó) antes de destruirse.
     * 
     * @return true si se agrietó al menos un bloque, false si no
     */
    public boolean destroyIceBlock(Position startPosition, Direction direction, GameMap map) {
        Position currentPos = startPosition.move(direction);
        boolean crackedAny = false;

        while (map.isValidPosition(currentPos)) {
            IceBlock iceBlock = getIceBlockAt(currentPos, map);

            if (iceBlock != null) {
                // Agrietar el bloque (inicia animación de ruptura)
                iceBlock.crack();
                crackedAny = true;

                // Verificar si hay una fogata en esta posición y apagarla
                Campfire campfire = getCampfireAt(currentPos, map);
                if (campfire != null && campfire.isLit()) {
                    campfire.extinguish();
                }
            } else {
                // Si encontramos un espacio sin hielo, dejamos de destruir
                break;
            }

            // Avanzar a la siguiente posición
            currentPos = currentPos.move(direction);
        }

        return crackedAny;
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

    /**
     * Verifica si hay una baldosa caliente en una posición.
     */
    public boolean hasHotTileAt(Position position, GameMap map) {
        return map.getEntities().stream()
                .filter(GameEntity::isActive)
                .filter(entity -> entity instanceof HotTile)
                .anyMatch(entity -> entity.getPosition().equals(position));
    }

    /**
     * Obtiene una fogata en una posición específica.
     */
    public Campfire getCampfireAt(Position position, GameMap map) {
        return map.getEntities().stream()
                .filter(GameEntity::isActive)
                .filter(entity -> entity instanceof Campfire)
                .map(entity -> (Campfire) entity)
                .filter(campfire -> campfire.getPosition().equals(position))
                .findFirst()
                .orElse(null);
    }
}
