package com.versionguard.platform;

import com.versionguard.config.VersionGuardConfig;

/**
 * Platform hooks used by shared command handling.
 */
public interface VersionGuardPlatform {

    VersionGuardConfig config();

    boolean reload();

    boolean viaVersionAvailable();

    String platformName();
}
