package com.versionguard.velocity;

import com.google.inject.Inject;
import com.versionguard.command.VersionGuardCommandService;
import com.versionguard.detect.ClientVersionResolver;
import com.versionguard.detect.ViaVersionProbe;
import com.versionguard.platform.VersionGuardRuntime;
import com.versionguard.velocity.via.VelocityViaVersionProbe;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.Path;

@Plugin(
        id = "versionguard",
        name = "VersionGuard",
        version = "1.0.0",
        description = "Restricts Minecraft Java Edition client versions to official releases.",
        authors = {"VersionGuard"},
        dependencies = {
                @Dependency(id = "viaversion", optional = true)
        }
)
public final class VersionGuardVelocityPlugin {

    private final ProxyServer server;
    private final Logger rawLogger;
    private final Path dataDirectory;
    private VersionGuardRuntime runtime;

    @Inject
    public VersionGuardVelocityPlugin(
            ProxyServer server,
            Logger logger,
            @DataDirectory Path dataDirectory
    ) {
        this.server = server;
        this.rawLogger = logger;
        this.dataDirectory = dataDirectory;
    }

    @Subscribe
    public void onProxyInitialize(ProxyInitializeEvent event) {
        VelocityPluginLogger logger = new VelocityPluginLogger(rawLogger);
        this.runtime = new VersionGuardRuntime(dataDirectory, logger, "Velocity");
        try {
            runtime.load();
        } catch (IOException exception) {
            logger.error("Could not load config.yml. VersionGuard will deny unsupported clients using defaults.", exception);
        }

        ViaVersionProbe viaProbe = VelocityViaVersionProbe.create(server.getPluginManager(), logger);
        runtime.viaVersionAvailable(viaProbe.available());
        ClientVersionResolver resolver = new ClientVersionResolver(viaProbe);
        VelocityPlayerLookup lookup = new VelocityPlayerLookup(server, runtime, resolver);
        VersionGuardCommandService commands = new VersionGuardCommandService(runtime, lookup);

        server.getEventManager().register(this, new VelocityVersionListener(runtime, resolver));

        CommandManager commandManager = server.getCommandManager();
        CommandMeta meta = commandManager.metaBuilder("versionguard")
                .aliases("vg")
                .plugin(this)
                .build();
        commandManager.register(meta, new VersionGuardVelocityCommand(commands));

        logger.info("VersionGuard is enforcing official Minecraft Java Edition releases from "
                + runtime.config().minimumVersion() + " to " + runtime.config().maximumVersion()
                + " before players reach a backend server.");
    }

    @Subscribe
    public void onProxyShutdown(ProxyShutdownEvent event) {
        if (runtime != null) {
            runtime.shutdown();
        }
    }
}
