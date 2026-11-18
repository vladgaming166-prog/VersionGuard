package com.versionguard.paper;

import com.versionguard.command.VersionGuardCommandService;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;

import java.util.List;

final class VersionGuardCommand implements TabExecutor {

    private final VersionGuardCommandService service;

    VersionGuardCommand(VersionGuardCommandService service) {
        this.service = service;
    }

    @Override
    public boolean onCommand(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            @NotNull String[] args
    ) {
        service.execute(new PaperCommandAudience(sender), args);
        return true;
    }

    @Override
    public @NotNull List<String> onTabComplete(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            @NotNull String[] args
    ) {
        return service.suggest(new PaperCommandAudience(sender), args);
    }
}
