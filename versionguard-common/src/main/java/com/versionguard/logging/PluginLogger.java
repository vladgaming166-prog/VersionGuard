package com.versionguard.logging;

/**
 * Platform-agnostic logger used by shared VersionGuard services.
 */
public interface PluginLogger {

    void info(String message);

    void warn(String message);

    void error(String message);

    void error(String message, Throwable throwable);

    void debug(String message);
}
