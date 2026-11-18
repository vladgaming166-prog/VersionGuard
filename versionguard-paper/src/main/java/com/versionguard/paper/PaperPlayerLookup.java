package com.versionguard.paper;

import com.versionguard.command.InspectedPlayer;
import com.versionguard.command.PlayerLookup;
import com.versionguard.detect.ClientVersionResolver;
import com.versionguard.platform.VersionGuardRuntime;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

final class PaperPlayerLookup implements PlayerLookup {

    private final VersionGuardRuntime runtime;
    private final ClientVersionResolver resolver;

    PaperPlayerLookup(VersionGuardRuntime runtime, ClientVersionResolver resolver) {
        this.runtime = runtime;
        this.resolver = resolver;
    }

    @Override
    public Optional<InspectedPlayer> find(String name) {
        Player player = Bukkit.getPlayerExact(name);
        if (player == null) {
            String needle = name.toLowerCase(Locale.ROOT);
            for (Player online : Bukkit.getOnlinePlayers()) {
                if (online.getName().toLowerCase(Locale.ROOT).equals(needle)) {
                    player = online;
                    break;
                }
            }
        }
        if (player == null) {
            return Optional.empty();
        }
        return Optional.of(inspect(player));
    }

    @Override
    public List<String> onlineNames() {
        return Bukkit.getOnlinePlayers().stream().map(Player::getName).toList();
    }

    InspectedPlayer inspect(Player player) {
        return new InspectedPlayer(
                player.getUniqueId(),
                player.getName(),
                resolver.resolve(player.getUniqueId(), player, player.getProtocolVersion()),
                player.hasPermission(runtime.config().bypassPermission())
        );
    }
}
