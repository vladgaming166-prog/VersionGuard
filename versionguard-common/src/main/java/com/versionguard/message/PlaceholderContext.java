package com.versionguard.message;

import com.versionguard.config.VersionGuardConfig;
import com.versionguard.protocol.ClientVersion;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Placeholder values substituted before MiniMessage deserialization.
 */
public final class PlaceholderContext {

    private final Map<String, String> values;

    private PlaceholderContext(Map<String, String> values) {
        this.values = Map.copyOf(values);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static PlaceholderContext of(String player, ClientVersion client, VersionGuardConfig config) {
        return builder()
                .player(player)
                .client(client)
                .config(config)
                .build();
    }

    public Map<String, String> values() {
        return values;
    }

    public String apply(String template) {
        if (template == null || template.isEmpty()) {
            return "";
        }
        String result = template;
        for (Map.Entry<String, String> entry : values.entrySet()) {
            result = result.replace(entry.getKey(), entry.getValue());
        }
        return result;
    }

    public static final class Builder {
        private final Map<String, String> values = new LinkedHashMap<>();

        private Builder() {
            values.put("%player%", "unknown");
            values.put("%client_version%", "unknown");
            values.put("%client_protocol%", "unknown");
            values.put("%minimum_version%", "1.21");
            values.put("%maximum_version%", "26.3");
        }

        public Builder player(String player) {
            values.put("%player%", player == null || player.isBlank() ? "unknown" : player);
            return this;
        }

        public Builder client(ClientVersion client) {
            Objects.requireNonNull(client, "client");
            values.put("%client_version%", client.displayName());
            values.put("%client_protocol%", client.protocolLabel());
            return this;
        }

        public Builder config(VersionGuardConfig config) {
            Objects.requireNonNull(config, "config");
            values.put("%minimum_version%", config.minimumVersion().toString());
            values.put("%maximum_version%", config.maximumVersion().toString());
            return this;
        }

        public Builder placeholder(String key, String value) {
            values.put(key, value == null ? "" : value);
            return this;
        }

        public PlaceholderContext build() {
            return new PlaceholderContext(values);
        }
    }
}
