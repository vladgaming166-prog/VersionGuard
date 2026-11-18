package com.versionguard.paper.via;

import com.versionguard.detect.AbsentViaVersionProbe;
import com.versionguard.detect.ViaVersionProbe;
import com.versionguard.logging.PluginLogger;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

/**
 * Loads ViaVersion only when the plugin is present so missing ViaVersion cannot crash VersionGuard.
 */
public final class PaperViaVersionProbe {

    private PaperViaVersionProbe() {
    }

    public static ViaVersionProbe create(PluginLogger logger) {
        Plugin via = Bukkit.getPluginManager().getPlugin("ViaVersion");
        if (via == null || !via.isEnabled()) {
            logger.warn("ViaVersion is not installed. VersionGuard will use Paper's native handshake protocol. "
                    + "Install ViaVersion from https://modrinth.com/plugin/viaversion for the most accurate "
                    + "client snapshot and experimental version detection.");
            return new AbsentViaVersionProbe();
        }
        try {
            Class.forName("com.viaversion.viaversion.api.Via");
            ViaVersionProbe probe = new PaperViaVersionAccess();
            if (!probe.available()) {
                logger.warn("ViaVersion was found but its API is not ready. Native protocol detection will be used.");
                return new AbsentViaVersionProbe();
            }
            logger.info("Hooked into ViaVersion " + via.getPluginMeta().getVersion() + ".");
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
