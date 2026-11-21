package com.versionguard.paper;

import com.versionguard.command.CommandAudience;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;

final class PaperCommandAudience implements CommandAudience {

    private final CommandSender sender;

    PaperCommandAudience(CommandSender sender) {
        this.sender = sender;
    }

    @Override
    public String name() {
        return sender.getName();
    }

    @Override
    public boolean hasPermission(String permission) {
        return sender.hasPermission(permission);
    }

    @Override
    public void sendMessage(Component message) {
        sender.sendMessage(message);
    }
}
