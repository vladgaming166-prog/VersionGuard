package com.versionguard.velocity;

import com.versionguard.detect.ClientVersionResolver;
import com.versionguard.platform.VersionGuardRuntime;
import com.versionguard.protocol.ClientVersion;
import com.versionguard.validation.ValidationResult;
import com.velocitypowered.api.event.ResultedEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.event.player.ServerPreConnectEvent;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;

final class VelocityVersionListener {

    private final VersionGuardRuntime runtime;
    private final ClientVersionResolver resolver;

    VelocityVersionListener(VersionGuardRuntime runtime, ClientVersionResolver resolver) {
        this.runtime = runtime;
        this.resolver = resolver;
    }

    @Subscribe(priority = Short.MAX_VALUE)
    public void onLogin(LoginEvent event) {
        Player player = event.getPlayer();
        ValidationResult result = evaluate(player);
        runtime.remember(player.getUniqueId(), result);
        if (!result.allowed()) {
            event.setResult(ResultedEvent.ComponentResult.denied(kickMessage(player.getUsername(), result.client())));
        }
    }

    @Subscribe(priority = Short.MAX_VALUE)
    public void onServerPreConnect(ServerPreConnectEvent event) {
        Player player = event.getPlayer();
        ValidationResult previous = runtime.previousOutcome(player.getUniqueId());
        if (previous instanceof ValidationResult.Allowed allowed && allowed.bypassed()) {
            return;
        }

        ValidationResult result = evaluate(player);
        runtime.remember(player.getUniqueId(), result);
        if (!result.allowed()) {
            Component kick = kickMessage(player.getUsername(), result.client());
            event.setResult(ServerPreConnectEvent.ServerResult.denied());
            player.disconnect(kick);
        }
    }

    @Subscribe(priority = Short.MIN_VALUE)
    public void onDisconnect(DisconnectEvent event) {
        runtime.forget(event.getPlayer().getUniqueId());
    }

    private ValidationResult evaluate(Player player) {
        boolean bypass = player.hasPermission(runtime.config().bypassPermission());
        int protocol = VelocityPlayerLookup.nativeProtocol(player);
        ClientVersion client = resolver.resolve(player.getUniqueId(), player, protocol);
        if (bypass) {
            runtime.logger().debug("Allowing " + player.getUsername() + " through versionguard.bypass");
        }
        return runtime.validate(client, bypass);
    }

    private Component kickMessage(String name, ClientVersion client) {
        return runtime.kickMessage(name, client);
    }
}
