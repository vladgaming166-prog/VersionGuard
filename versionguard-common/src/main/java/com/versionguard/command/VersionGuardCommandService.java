package com.versionguard.command;

import com.versionguard.VersionGuard;
import com.versionguard.config.VersionGuardConfig;
import com.versionguard.message.PlaceholderContext;
import com.versionguard.platform.VersionGuardRuntime;
import com.versionguard.protocol.ClientVersion;
import com.versionguard.validation.ValidationResult;
import net.kyori.adventure.text.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * Shared {@code /versionguard} command implementation.
 */
public final class VersionGuardCommandService {

    private static final List<String> SUBCOMMANDS = List.of("reload", "info", "check");

    private final VersionGuardRuntime runtime;
    private final PlayerLookup playerLookup;

    public VersionGuardCommandService(VersionGuardRuntime runtime, PlayerLookup playerLookup) {
        this.runtime = runtime;
        this.playerLookup = playerLookup;
    }

    public void execute(CommandAudience audience, String[] args) {
        VersionGuardConfig config = runtime.config();
        if (!audience.hasPermission(config.adminPermission())) {
            audience.sendMessage(runtime.commandMessage(
                    config.noPermissionMessage(),
                    PlaceholderContext.builder().player(audience.name()).config(config).build()
            ));
            return;
        }

        if (args.length == 0) {
            audience.sendMessage(runtime.commandMessage(
                    config.usageMessage(),
                    PlaceholderContext.builder().player(audience.name()).config(config).build()
            ));
            return;
        }

        switch (args[0].toLowerCase(Locale.ROOT)) {
            case "reload" -> handleReload(audience, config);
            case "info" -> handleInfo(audience, config);
            case "check" -> handleCheck(audience, config, args);
            default -> audience.sendMessage(runtime.commandMessage(
                    config.usageMessage(),
                    PlaceholderContext.builder().player(audience.name()).config(config).build()
            ));
        }
    }

    public List<String> suggest(CommandAudience audience, String[] args) {
        VersionGuardConfig config = runtime.config();
        if (!audience.hasPermission(config.adminPermission())) {
            return List.of();
        }
        if (args.length <= 1) {
            String prefix = args.length == 0 ? "" : args[0].toLowerCase(Locale.ROOT);
            return SUBCOMMANDS.stream()
                    .filter(command -> command.startsWith(prefix))
                    .toList();
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("check")) {
            String prefix = args[1].toLowerCase(Locale.ROOT);
            List<String> names = new ArrayList<>();
            for (String name : playerLookup.onlineNames()) {
                if (name.toLowerCase(Locale.ROOT).startsWith(prefix)) {
                    names.add(name);
                }
            }
            return names;
        }
        return List.of();
    }

    public boolean hasPermission(CommandAudience audience) {
        return audience.hasPermission(runtime.config().adminPermission());
    }

    private void handleReload(CommandAudience audience, VersionGuardConfig config) {
        boolean success = runtime.reload();
        String template = success ? config.reloadSuccessMessage() : config.reloadFailedMessage();
        audience.sendMessage(runtime.commandMessage(
                template,
                PlaceholderContext.builder().player(audience.name()).config(runtime.config()).build()
        ));
    }

    private void handleInfo(CommandAudience audience, VersionGuardConfig config) {
        audience.sendMessage(Component.text()
                .append(runtime.commandMessage(
                        "<gold><bold>VersionGuard</bold></gold> <gray>" + VersionGuard.VERSION + "</gray>",
                        PlaceholderContext.builder().config(config).build()))
                .appendNewline()
                .append(runtime.commandMessage(
                        "<gray>Platform:</gray> <white>" + runtime.platformName() + "</white>",
                        PlaceholderContext.builder().config(config).build()))
                .appendNewline()
                .append(runtime.commandMessage(
                        "<gray>Allowed versions:</gray> <green>%minimum_version%</green> <gray>-</gray> <green>%maximum_version%</green>",
                        PlaceholderContext.builder().config(config).build()))
                .appendNewline()
                .append(runtime.commandMessage(
                        "<gray>Block snapshots:</gray> <white>" + config.blockSnapshots() + "</white>",
                        PlaceholderContext.builder().config(config).build()))
                .appendNewline()
                .append(runtime.commandMessage(
                        "<gray>Block experimental:</gray> <white>" + config.blockExperimental() + "</white>",
                        PlaceholderContext.builder().config(config).build()))
                .appendNewline()
                .append(runtime.commandMessage(
                        "<gray>Block unknown protocols:</gray> <white>" + config.blockUnknownProtocols() + "</white>",
                        PlaceholderContext.builder().config(config).build()))
                .appendNewline()
                .append(runtime.commandMessage(
                        "<gray>ViaVersion:</gray> <white>"
                                + (runtime.viaVersionAvailable() ? "detected" : "not installed")
                                + "</white>",
                        PlaceholderContext.builder().config(config).build()))
                .build());
    }

    private void handleCheck(CommandAudience audience, VersionGuardConfig config, String[] args) {
        if (args.length < 2) {
            audience.sendMessage(runtime.commandMessage(
                    config.usageMessage(),
                    PlaceholderContext.builder().player(audience.name()).config(config).build()
            ));
            return;
        }

        Optional<InspectedPlayer> found = playerLookup.find(args[1]);
        if (found.isEmpty()) {
            audience.sendMessage(runtime.commandMessage(
                    config.playerNotFoundMessage(),
                    PlaceholderContext.builder().player(args[1]).config(config).build()
            ));
            return;
        }

        InspectedPlayer player = found.get();
        ClientVersion version = player.version();
        ValidationResult result = runtime.validate(version, player.bypass());
        String status = result.allowed()
                ? (player.bypass() ? "<yellow>bypassed</yellow>" : "<green>allowed</green>")
                : "<red>blocked (" + result.reason().map(reason -> reason.description()).orElse("denied") + ")</red>";

        PlaceholderContext context = PlaceholderContext.of(player.name(), version, config);
        audience.sendMessage(Component.text()
                .append(runtime.commandMessage("<gold>VersionGuard check</gold> <gray>for</gray> <white>%player%</white>", context))
                .appendNewline()
                .append(runtime.commandMessage("<gray>Client:</gray> <white>%client_version%</white>", context))
                .appendNewline()
                .append(runtime.commandMessage("<gray>Protocol:</gray> <white>%client_protocol%</white>", context))
                .appendNewline()
                .append(runtime.commandMessage("<gray>Snapshot:</gray> <white>" + version.snapshot() + "</white>", context))
                .appendNewline()
                .append(runtime.commandMessage("<gray>Experimental:</gray> <white>" + version.experimental() + "</white>", context))
                .appendNewline()
                .append(runtime.commandMessage("<gray>Bypass permission:</gray> <white>" + player.bypass() + "</white>", context))
                .appendNewline()
                .append(runtime.commandMessage("<gray>Decision:</gray> " + status, context))
                .build());
    }
}
