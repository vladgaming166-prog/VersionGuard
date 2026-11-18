package com.versionguard.config;

import com.versionguard.VersionGuard;
import com.versionguard.protocol.OfficialProtocol;
import com.versionguard.protocol.OfficialProtocolCatalog;
import com.versionguard.version.MinecraftVersion;

import java.util.List;
import java.util.Objects;

/**
 * Immutable runtime configuration for VersionGuard.
 */
public final class VersionGuardConfig {

    private final MinecraftVersion minimumVersion;
    private final MinecraftVersion maximumVersion;
    private final OfficialProtocol minimumProtocol;
    private final OfficialProtocol maximumProtocol;
    private final boolean blockSnapshots;
    private final boolean blockExperimental;
    private final boolean blockUnknownProtocols;
    private final boolean debug;
    private final List<String> kickLines;
    private final String noPermissionMessage;
    private final String reloadSuccessMessage;
    private final String reloadFailedMessage;
    private final String playerNotFoundMessage;
    private final String usageMessage;
    private final String bypassPermission;
    private final String adminPermission;

    private VersionGuardConfig(Builder builder) {
        this.minimumVersion = builder.minimumVersion;
        this.maximumVersion = builder.maximumVersion;
        this.minimumProtocol = builder.minimumProtocol;
        this.maximumProtocol = builder.maximumProtocol;
        this.blockSnapshots = builder.blockSnapshots;
        this.blockExperimental = builder.blockExperimental;
        this.blockUnknownProtocols = builder.blockUnknownProtocols;
        this.debug = builder.debug;
        this.kickLines = List.copyOf(builder.kickLines);
        this.noPermissionMessage = builder.noPermissionMessage;
        this.reloadSuccessMessage = builder.reloadSuccessMessage;
        this.reloadFailedMessage = builder.reloadFailedMessage;
        this.playerNotFoundMessage = builder.playerNotFoundMessage;
        this.usageMessage = builder.usageMessage;
        this.bypassPermission = builder.bypassPermission;
        this.adminPermission = builder.adminPermission;
    }

    public static VersionGuardConfig defaults() {
        return builder().build();
    }

    public static Builder builder() {
        return new Builder();
    }

    public MinecraftVersion minimumVersion() {
        return minimumVersion;
    }

    public MinecraftVersion maximumVersion() {
        return maximumVersion;
    }

    public OfficialProtocol minimumProtocol() {
        return minimumProtocol;
    }

    public OfficialProtocol maximumProtocol() {
        return maximumProtocol;
    }

    public boolean blockSnapshots() {
        return blockSnapshots;
    }

    public boolean blockExperimental() {
        return blockExperimental;
    }

    public boolean blockUnknownProtocols() {
        return blockUnknownProtocols;
    }

    public boolean debug() {
        return debug;
    }

    public List<String> kickLines() {
        return kickLines;
    }

    public String noPermissionMessage() {
        return noPermissionMessage;
    }

    public String reloadSuccessMessage() {
        return reloadSuccessMessage;
    }

    public String reloadFailedMessage() {
        return reloadFailedMessage;
    }

    public String playerNotFoundMessage() {
        return playerNotFoundMessage;
    }

    public String usageMessage() {
        return usageMessage;
    }

    public String bypassPermission() {
        return bypassPermission;
    }

    public String adminPermission() {
        return adminPermission;
    }

    public static final class Builder {
        private MinecraftVersion minimumVersion = MinecraftVersion.parse(VersionGuard.DEFAULT_MINIMUM_VERSION);
        private MinecraftVersion maximumVersion = MinecraftVersion.parse(VersionGuard.DEFAULT_MAXIMUM_VERSION);
        private OfficialProtocol minimumProtocol = OfficialProtocolCatalog.findByVersion(minimumVersion).orElseThrow();
        private OfficialProtocol maximumProtocol = OfficialProtocolCatalog.findByVersion(maximumVersion).orElseThrow();
        private boolean blockSnapshots = true;
        private boolean blockExperimental = true;
        private boolean blockUnknownProtocols = true;
        private boolean debug = false;
        private List<String> kickLines = List.of(
                "<red><bold>VersionGuard</bold></red>",
                "",
                "<gray>Your Minecraft version is not supported.</gray>",
                "",
                "<white>Minimum version: <green>1.21</green></white>",
                "<white>Maximum version: <green>26.3</green></white>",
                "",
                "<gray>Your version: <red>%client_version%</red></gray>",
                "",
                "<yellow>Please update your Minecraft client.</yellow>"
        );
        private String noPermissionMessage = "<red>You do not have permission to use VersionGuard commands.</red>";
        private String reloadSuccessMessage = "<green>VersionGuard configuration reloaded.</green>";
        private String reloadFailedMessage = "<red>VersionGuard configuration reload failed. The previous configuration is still active.</red>";
        private String playerNotFoundMessage = "<red>Player <white>%player%</white> is not online.</red>";
        private String usageMessage = "<gray>Usage:</gray> <white>/versionguard reload</white><gray>,</gray> <white>/versionguard info</white><gray>, or</gray> <white>/versionguard check PLAYER</white>";
        private String bypassPermission = VersionGuard.DEFAULT_BYPASS_PERMISSION;
        private String adminPermission = VersionGuard.DEFAULT_ADMIN_PERMISSION;

        private Builder() {
        }

        public Builder minimumVersion(MinecraftVersion minimumVersion) {
            this.minimumVersion = Objects.requireNonNull(minimumVersion, "minimumVersion");
            this.minimumProtocol = OfficialProtocolCatalog.findByVersion(minimumVersion).orElseThrow(() ->
                    new IllegalArgumentException("Minimum version is not an official mapped release: " + minimumVersion));
            return this;
        }

        public Builder maximumVersion(MinecraftVersion maximumVersion) {
            this.maximumVersion = Objects.requireNonNull(maximumVersion, "maximumVersion");
            this.maximumProtocol = OfficialProtocolCatalog.findByVersion(maximumVersion).orElseThrow(() ->
                    new IllegalArgumentException("Maximum version is not an official mapped release: " + maximumVersion));
            return this;
        }

        public Builder blockSnapshots(boolean blockSnapshots) {
            this.blockSnapshots = blockSnapshots;
            return this;
        }

        public Builder blockExperimental(boolean blockExperimental) {
            this.blockExperimental = blockExperimental;
            return this;
        }

        public Builder blockUnknownProtocols(boolean blockUnknownProtocols) {
            this.blockUnknownProtocols = blockUnknownProtocols;
            return this;
        }

        public Builder debug(boolean debug) {
            this.debug = debug;
            return this;
        }

        public Builder kickLines(List<String> kickLines) {
            this.kickLines = List.copyOf(Objects.requireNonNull(kickLines, "kickLines"));
            return this;
        }

        public Builder noPermissionMessage(String noPermissionMessage) {
            this.noPermissionMessage = requireText(noPermissionMessage, "noPermissionMessage");
            return this;
        }

        public Builder reloadSuccessMessage(String reloadSuccessMessage) {
            this.reloadSuccessMessage = requireText(reloadSuccessMessage, "reloadSuccessMessage");
            return this;
        }

        public Builder reloadFailedMessage(String reloadFailedMessage) {
            this.reloadFailedMessage = requireText(reloadFailedMessage, "reloadFailedMessage");
            return this;
        }

        public Builder playerNotFoundMessage(String playerNotFoundMessage) {
            this.playerNotFoundMessage = requireText(playerNotFoundMessage, "playerNotFoundMessage");
            return this;
        }

        public Builder usageMessage(String usageMessage) {
            this.usageMessage = requireText(usageMessage, "usageMessage");
            return this;
        }

        public Builder bypassPermission(String bypassPermission) {
            this.bypassPermission = requireText(bypassPermission, "bypassPermission");
            return this;
        }

        public Builder adminPermission(String adminPermission) {
            this.adminPermission = requireText(adminPermission, "adminPermission");
            return this;
        }

        public VersionGuardConfig build() {
            if (minimumVersion.compareTo(maximumVersion) > 0) {
                throw new IllegalArgumentException(
                        "Minimum version " + minimumVersion + " is newer than maximum version " + maximumVersion);
            }
            if (kickLines.isEmpty()) {
                throw new IllegalArgumentException("Kick message must contain at least one line");
            }
            return new VersionGuardConfig(this);
        }

        private static String requireText(String value, String name) {
            if (value == null || value.isBlank()) {
                throw new IllegalArgumentException(name + " must not be blank");
            }
            return value;
        }
    }
}
