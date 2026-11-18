package com.versionguard.paper;

import com.versionguard.detect.ClientVersionResolver;
import com.versionguard.platform.VersionGuardRuntime;
import com.versionguard.protocol.ClientVersion;
import com.versionguard.validation.ValidationResult;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

@SuppressWarnings("deprecation")
final class PaperVersionListener implements Listener {

    private final VersionGuardRuntime runtime;
    private final ClientVersionResolver resolver;

    PaperVersionListener(VersionGuardRuntime runtime, ClientVersionResolver resolver) {
        this.runtime = runtime;
        this.resolver = resolver;
    }

    /**
     * PlayerLoginEvent is the earliest Paper event that exposes both the
     * client's protocol version and permission data. The replacement
     * PlayerConnectionValidateLoginEvent does not provide protocol information.
     */
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onLogin(PlayerLoginEvent event) {
        Player player = event.getPlayer();
        ValidationResult result = evaluate(player);
        runtime.remember(player.getUniqueId(), result);
        if (!result.allowed()) {
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, kickMessage(player.getName(), result.client()));
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID uniqueId = player.getUniqueId();
        ValidationResult previous = runtime.previousOutcome(uniqueId);
        if (previous != null && previous.allowed() && previous instanceof ValidationResult.Allowed allowed && allowed.bypassed()) {
            return;
        }

        ValidationResult result = evaluate(player);
        runtime.remember(uniqueId, result);
        if (!result.allowed()) {
            player.kick(kickMessage(player.getName(), result.client()));
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent event) {
        runtime.forget(event.getPlayer().getUniqueId());
    }

    private ValidationResult evaluate(Player player) {
        boolean bypass = player.hasPermission(runtime.config().bypassPermission());
        int protocol = player.getProtocolVersion();
        ClientVersion client = resolver.resolve(player.getUniqueId(), player, protocol);
        if (bypass) {
            runtime.logger().debug("Allowing " + player.getName() + " through versionguard.bypass");
        }
        return runtime.validate(client, bypass);
    }

    private Component kickMessage(String name, ClientVersion client) {
        return runtime.kickMessage(name, client);
    }
}
