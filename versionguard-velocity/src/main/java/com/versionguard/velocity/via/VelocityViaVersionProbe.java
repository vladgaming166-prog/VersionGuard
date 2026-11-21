package com.versionguard.velocity.via;

import com.versionguard.detect.AbsentViaVersionProbe;
import com.versionguard.detect.ViaVersionProbe;
import com.versionguard.logging.PluginLogger;
import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.plugin.PluginManager;

/**
 * Loads ViaVersion only when the Velocity plugin is present.
 */
public final class VelocityViaVersionProbe {

    private VelocityViaVersionProbe() {
    }

    public static ViaVersionProbe create(PluginManager pluginManager, PluginLogger logger) {
        PluginContainer container = pluginManager.getPlugin("viaversion").orElse(null);
        if (container == null) {
            logger.warn("ViaVersion is not installed on this proxy. VersionGuard will use Velocity's native "
                    + "handshake protocol. Install ViaVersion from https://modrinth.com/plugin/viaversion "
                    + "for the most accurate client snapshot and experimental version detection.");
            return new AbsentViaVersionProbe();
        }
        try {
            Class.forName("com.viaversion.viaversion.api.Via");
            ViaVersionProbe probe = new VelocityViaVersionAccess();
            if (!probe.available()) {
                logger.warn("ViaVersion was found but its API is not ready. Native protocol detection will be used.");
                return new AbsentViaVersionProbe();
            }
            String version = container.getDescription().getVersion().orElse("unknown");
            logger.info("Hooked into ViaVersion " + version + ".");
            return probe;
        } catch (ClassNotFoundException exception) {
            logger.warn("ViaVersion is installed but com.viaversion.viaversion.api.Via is not on the classpath. "
                    + "Native protocol detection will be used.");
            return new AbsentViaVersionProbe();
        } catch (Throwable throwable) {
            logger.error("Failed to initialize ViaVersion integration. Native protocol detection will be used.", throwable);
            return new AbsentViaVersionProbe();
        }
    }
}
