package com.versionguard.paper;

import com.versionguard.logging.PluginLogger;

import java.util.logging.Level;
import java.util.logging.Logger;

final class PaperPluginLogger implements PluginLogger {

    private final Logger logger;

    PaperPluginLogger(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void info(String message) {
        logger.info(message);
    }

    @Override
    public void warn(String message) {
        logger.warning(message);
    }

    @Override
    public void error(String message) {
        logger.severe(message);
    }

    @Override
    public void error(String message, Throwable throwable) {
        logger.log(Level.SEVERE, message, throwable);
    }

    @Override
    public void debug(String message) {
        logger.info("[debug] " + message);
    }
}
