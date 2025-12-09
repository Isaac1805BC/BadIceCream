package com.badice.domain.exceptions;

/**
 * Excepción lanzada cuando hay un error en la configuración del juego o
 * niveles.
 */
public class InvalidConfigurationException extends GameException {
    private static final long serialVersionUID = 1L;

    private final String configKey;
    private final String configValue;

    public InvalidConfigurationException(String message) {
        super(message, "INVALID_CONFIG");
        this.configKey = null;
        this.configValue = null;
    }

    public InvalidConfigurationException(String message, String configKey, String configValue) {
        super(message, "INVALID_CONFIG");
        this.configKey = configKey;
        this.configValue = configValue;
    }

    public InvalidConfigurationException(String message, Throwable cause) {
        super(message, "INVALID_CONFIG", cause);
        this.configKey = null;
        this.configValue = null;
    }

    public String getConfigKey() {
        return configKey;
    }

    public String getConfigValue() {
        return configValue;
    }

    @Override
    public String toString() {
        if (configKey != null && configValue != null) {
            return String.format("[INVALID_CONFIG] Invalid configuration '%s'='%s': %s",
                    configKey, configValue, getMessage());
        }
        return super.toString();
    }
}
