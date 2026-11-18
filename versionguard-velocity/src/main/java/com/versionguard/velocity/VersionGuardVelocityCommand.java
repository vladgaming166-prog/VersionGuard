package com.versionguard.velocity;

import com.versionguard.command.VersionGuardCommandService;
import com.velocitypowered.api.command.SimpleCommand;

import java.util.List;
import java.util.concurrent.CompletableFuture;

final class VersionGuardVelocityCommand implements SimpleCommand {

    private final VersionGuardCommandService service;

    VersionGuardVelocityCommand(VersionGuardCommandService service) {
        this.service = service;
    }

    @Override
    public void execute(Invocation invocation) {
        service.execute(new VelocityCommandAudience(invocation.source()), invocation.arguments());
    }

    @Override
    public List<String> suggest(Invocation invocation) {
        return service.suggest(new VelocityCommandAudience(invocation.source()), invocation.arguments());
    }

    @Override
    public CompletableFuture<List<String>> suggestAsync(Invocation invocation) {
        return CompletableFuture.completedFuture(suggest(invocation));
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return service.hasPermission(new VelocityCommandAudience(invocation.source()));
    }
}
