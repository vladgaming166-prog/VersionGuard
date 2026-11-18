package com.versionguard.velocity;

import com.versionguard.command.InspectedPlayer;
import com.versionguard.command.PlayerLookup;
import com.versionguard.detect.ClientVersionResolver;
import com.versionguard.platform.VersionGuardRuntime;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;

import java.util.List;
import java.util.Optional;

final class VelocityPlayerLookup implements PlayerLookup {

    private final ProxyServer server;
    private final VersionGuardRuntime runtime;
    private final ClientVersionResolver resolver;

    VelocityPlayerLookup(ProxyServer server, VersionGuardRuntime runtime, ClientVersionResolver resolver) {
        this.server = server;
        this.runtime = runtime;
        this.resolver = resolver;
    }

    @Override
    public Optional<InspectedPlayer> find(String name) {
        return server.getPlayer(name).map(this::inspect);
    }

    @Override
    public List<String> onlineNames() {
        return server.getAllPlayers().stream().map(Player::getUsername).toList();
    }

    InspectedPlayer inspect(Player player) {
        return new InspectedPlayer(
                player.getUniqueId(),
                player.getUsername(),
                resolver.resolve(player.getUniqueId(), player, nativeProtocol(player)),
                player.hasPermission(runtime.config().bypassPermission())
        );
    }

    static int nativeProtocol(Player player) {
        try {
            return player.getProtocolVersion().getProtocol();
        } catch (RuntimeException exception) {
            return -1;
        }
    }
}
