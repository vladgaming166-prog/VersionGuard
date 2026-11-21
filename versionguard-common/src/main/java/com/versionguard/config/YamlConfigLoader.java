package com.versionguard.config;

import com.versionguard.VersionGuard;
import com.versionguard.version.MinecraftVersion;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Loads {@link VersionGuardConfig} from a YAML document.
 */
public final class YamlConfigLoader {

    private static final String CLASSPATH_CONFIG = "/config.yml";

    private final Yaml yaml = new Yaml();

    public VersionGuardConfig load(Path file) throws IOException {
        try (Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            return load(reader);
        }
    }

    public VersionGuardConfig load(Reader reader) {
        Object loaded = yaml.load(reader);
        return fromRoot(asMap(loaded));
    }

    public VersionGuardConfig load(InputStream stream) {
        Object loaded = yaml.load(stream);
        return fromRoot(asMap(loaded));
    }

    public VersionGuardConfig defaultsFromClasspath() {
        try (InputStream stream = YamlConfigLoader.class.getResourceAsStream(CLASSPATH_CONFIG)) {
            if (stream == null) {
                return VersionGuardConfig.defaults();
            }
            return load(stream);
        } catch (Exception exception) {
            return VersionGuardConfig.defaults();
        }
    }

    public void copyDefaultIfMissing(Path destination) throws IOException {
        if (Files.exists(destination)) {
            return;
        }
        Files.createDirectories(destination.getParent());
        try (InputStream stream = YamlConfigLoader.class.getResourceAsStream(CLASSPATH_CONFIG)) {
            if (stream == null) {
                throw new IOException("Missing bundled config.yml");
            }
            Files.copy(stream, destination);
        }
    }

    public VersionGuardConfig fromRoot(Map<String, Object> root) {
        VersionGuardConfig.Builder builder = VersionGuardConfig.builder();
        Map<String, Object> settings = child(root, "settings");
        Map<String, Object> messages = child(root, "messages");
        Map<String, Object> commandMessages = child(messages, "command");
        Map<String, Object> permissions = child(root, "permissions");

        String minimum = stringValue(settings, "minimum-version", VersionGuard.DEFAULT_MINIMUM_VERSION);
        String maximum = stringValue(settings, "maximum-version", VersionGuard.DEFAULT_MAXIMUM_VERSION);
        builder.minimumVersion(parseRequiredVersion("settings.minimum-version", minimum));
        builder.maximumVersion(parseRequiredVersion("settings.maximum-version", maximum));
        builder.blockSnapshots(booleanValue(settings, "block-snapshots", true));
        builder.blockExperimental(booleanValue(settings, "block-experimental", true));
        builder.blockUnknownProtocols(booleanValue(settings, "block-unknown-protocols", true));
        builder.debug(booleanValue(settings, "debug", false));

        List<String> kickLines = stringList(messages, "kick");
        if (!kickLines.isEmpty()) {
            builder.kickLines(kickLines);
        }

        builder.noPermissionMessage(stringValue(
                commandMessages, "no-permission",
                "<red>You do not have permission to use VersionGuard commands.</red>"));
        builder.reloadSuccessMessage(stringValue(
                commandMessages, "reload-success",
                "<green>VersionGuard configuration reloaded.</green>"));
        builder.reloadFailedMessage(stringValue(
                commandMessages, "reload-failed",
                "<red>VersionGuard configuration reload failed. The previous configuration is still active.</red>"));
        builder.playerNotFoundMessage(stringValue(
                commandMessages, "player-not-found",
                "<red>Player <white>%player%</white> is not online.</red>"));
        builder.usageMessage(stringValue(
                commandMessages, "usage",
                "<gray>Usage:</gray> <white>/versionguard reload</white><gray>,</gray> <white>/versionguard info</white><gray>, or</gray> <white>/versionguard check PLAYER</white>"));

        builder.bypassPermission(stringValue(permissions, "bypass", VersionGuard.DEFAULT_BYPASS_PERMISSION));
        builder.adminPermission(stringValue(permissions, "admin", VersionGuard.DEFAULT_ADMIN_PERMISSION));
        return builder.build();
    }

    private static MinecraftVersion parseRequiredVersion(String path, String raw) {
        return MinecraftVersion.tryParse(raw).orElseThrow(() ->
                new IllegalArgumentException("Invalid version at " + path + ": " + raw));
    }

    private static Map<String, Object> child(Map<String, Object> parent, String key) {
        Object value = parent.get(key);
        return asMap(value);
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> asMap(Object value) {
        if (value instanceof Map<?, ?> map) {
            Map<String, Object> copy = new LinkedHashMap<>();
            map.forEach((key, nested) -> copy.put(String.valueOf(key), nested));
            return copy;
        }
        return new LinkedHashMap<>();
    }

    private static String stringValue(Map<String, Object> map, String key, String fallback) {
        Object value = map.get(key);
        if (value == null) {
            return fallback;
        }
        String text = Objects.toString(value, "").trim();
        return text.isEmpty() ? fallback : text;
    }

    private static boolean booleanValue(Map<String, Object> map, String key, boolean fallback) {
        Object value = map.get(key);
        if (value instanceof Boolean bool) {
            return bool;
        }
        if (value instanceof String text) {
            if (text.equalsIgnoreCase("true") || text.equalsIgnoreCase("yes") || text.equalsIgnoreCase("on")) {
                return true;
            }
            if (text.equalsIgnoreCase("false") || text.equalsIgnoreCase("no") || text.equalsIgnoreCase("off")) {
                return false;
            }
        }
        return fallback;
    }

    private static List<String> stringList(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value instanceof List<?> list) {
            List<String> lines = new ArrayList<>();
            for (Object entry : list) {
                lines.add(entry == null ? "" : Objects.toString(entry));
            }
            return lines;
        }
        if (value instanceof String text) {
            return List.of(text);
        }
        return List.of();
    }
}
