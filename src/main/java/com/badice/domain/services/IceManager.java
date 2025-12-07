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
                System.out.println("Ice melted instantly on hot tile at " + currentPos);
                // No crear hielo, pero continuar la línea
                currentPos = currentPos.move(direction);
                continue;
            }

            // Crear el bloque de hielo (incluso sobre fogatas)
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

                // NUEVO: Verificar si hay una fogata en esta posición y apagarla
                Campfire campfire = getCampfireAt(currentPos, map);
                if (campfire != null && campfire.isLit()) {
                    campfire.extinguish();
                    System.out.println("Campfire extinguished by destroying ice at " + currentPos);
                }
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
