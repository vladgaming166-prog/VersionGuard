package com.versionguard.velocity;

import com.versionguard.logging.PluginLogger;
import org.slf4j.Logger;

final class VelocityPluginLogger implements PluginLogger {

    private final Logger logger;

    VelocityPluginLogger(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void info(String message) {
        logger.info(message);
    }

    @Override
    public void warn(String message) {
        logger.warn(message);
    }

    @Override
    public void error(String message) {
        logger.error(message);
    }

    @Override
    public void error(String message, Throwable throwable) {
        logger.error(message, throwable);
    }

    @Override
    public void debug(String message) {
        logger.info("[debug] {}", message);
    }
}
