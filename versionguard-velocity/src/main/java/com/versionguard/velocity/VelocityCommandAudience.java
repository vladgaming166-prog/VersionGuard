package com.versionguard.velocity;

import com.versionguard.command.CommandAudience;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;

final class VelocityCommandAudience implements CommandAudience {

    private final CommandSource source;

    VelocityCommandAudience(CommandSource source) {
        this.source = source;
    }

    @Override
    public String name() {
        if (source instanceof Player player) {
            return player.getUsername();
        }
        return "Console";
    }

    @Override
    public boolean hasPermission(String permission) {
        return source.hasPermission(permission);
    }

    @Override
    public void sendMessage(Component message) {
        source.sendMessage(message);
    }
}
