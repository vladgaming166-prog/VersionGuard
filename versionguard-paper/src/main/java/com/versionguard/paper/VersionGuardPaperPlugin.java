package com.versionguard.paper;

import com.versionguard.command.VersionGuardCommandService;
import com.versionguard.detect.ClientVersionResolver;
import com.versionguard.detect.ViaVersionProbe;
import com.versionguard.paper.via.PaperViaVersionProbe;
import com.versionguard.platform.VersionGuardRuntime;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

public final class VersionGuardPaperPlugin extends JavaPlugin {

    private VersionGuardRuntime runtime;

    @Override
    public void onEnable() {
        PaperPluginLogger logger = new PaperPluginLogger(getLogger());
        this.runtime = new VersionGuardRuntime(getDataFolder().toPath(), logger, "Paper/Purpur");
        try {
            runtime.load();
        } catch (IOException exception) {
            logger.error("Could not load config.yml. VersionGuard will deny unsupported clients using defaults.", exception);
        }

        ViaVersionProbe viaProbe = PaperViaVersionProbe.create(logger);
        runtime.viaVersionAvailable(viaProbe.available());
        ClientVersionResolver resolver = new ClientVersionResolver(viaProbe);
        PaperPlayerLookup lookup = new PaperPlayerLookup(runtime, resolver);
        VersionGuardCommandService commands = new VersionGuardCommandService(runtime, lookup);

        getServer().getPluginManager().registerEvents(new PaperVersionListener(runtime, resolver), this);
        PluginCommand command = getCommand("versionguard");
        if (command == null) {
            logger.error("The /versionguard command is missing from plugin.yml.");
        } else {
            VersionGuardCommand executor = new VersionGuardCommand(commands);
            command.setExecutor(executor);
            command.setTabCompleter(executor);
            command.setPermission(runtime.config().adminPermission());
        }

        logger.info("VersionGuard is enforcing official Minecraft Java Edition releases from "
                + runtime.config().minimumVersion() + " to " + runtime.config().maximumVersion() + ".");
    }

    @Override
    public void onDisable() {
        if (runtime != null) {
            runtime.shutdown();
        }
    }
}
