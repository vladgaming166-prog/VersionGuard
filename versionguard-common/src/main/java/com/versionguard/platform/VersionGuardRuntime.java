package com.versionguard.platform;

import com.versionguard.config.VersionGuardConfig;
import com.versionguard.config.YamlConfigLoader;
import com.versionguard.logging.PluginLogger;
import com.versionguard.message.MessageService;
import com.versionguard.message.PlaceholderContext;
import com.versionguard.protocol.ClientVersion;
import com.versionguard.validation.ValidationResult;
import com.versionguard.validation.VersionValidator;
import net.kyori.adventure.text.Component;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Process-wide VersionGuard state shared by a single plugin instance.
 */
public final class VersionGuardRuntime implements VersionGuardPlatform {

    private final Path dataDirectory;
    private final Path configFile;
    private final PluginLogger logger;
    private final YamlConfigLoader configLoader;
    private final VersionValidator validator;
    private final MessageService messages;
    private final String platformName;
    private final ConcurrentMap<UUID, ValidationResult> outcomes = new ConcurrentHashMap<>();

    private volatile VersionGuardConfig config;
    private volatile boolean viaVersionAvailable;

    public VersionGuardRuntime(
            Path dataDirectory,
            PluginLogger logger,
            String platformName
    ) {
        this.dataDirectory = Objects.requireNonNull(dataDirectory, "dataDirectory");
        this.configFile = dataDirectory.resolve("config.yml");
        this.logger = Objects.requireNonNull(logger, "logger");
        this.configLoader = new YamlConfigLoader();
        this.validator = new VersionValidator();
        this.messages = new MessageService();
        this.platformName = Objects.requireNonNull(platformName, "platformName");
        this.config = VersionGuardConfig.defaults();
    }

    public void load() throws IOException {
        configLoader.copyDefaultIfMissing(configFile);
        this.config = configLoader.load(configFile);
        logger.info("Loaded configuration: " + config.minimumVersion() + " - " + config.maximumVersion());
    }

    @Override
    public boolean reload() {
        try {
            this.config = configLoader.load(configFile);
            logger.info("Reloaded configuration: " + config.minimumVersion() + " - " + config.maximumVersion());
            return true;
        } catch (Exception exception) {
            logger.error("Failed to reload config.yml; keeping the previous configuration.", exception);
            return false;
        }
    }

    @Override
    public VersionGuardConfig config() {
        return config;
    }

    @Override
    public boolean viaVersionAvailable() {
        return viaVersionAvailable;
    }

    public void viaVersionAvailable(boolean viaVersionAvailable) {
        this.viaVersionAvailable = viaVersionAvailable;
    }

    @Override
    public String platformName() {
        return platformName;
    }

    public PluginLogger logger() {
        return logger;
    }

    public MessageService messages() {
        return messages;
    }

    public Path dataDirectory() {
        return dataDirectory;
    }

    public ValidationResult validate(ClientVersion client, boolean bypass) {
        ValidationResult result = validator.validate(client, config, bypass);
        if (config.debug()) {
            logger.debug("Validated " + client.displayName()
                    + " protocol=" + client.protocolLabel()
                    + " snapshot=" + client.snapshot()
                    + " experimental=" + client.experimental()
                    + " unknown=" + client.unknownProtocol()
                    + " bypass=" + bypass
                    + " result=" + (result.allowed() ? "allowed" : result.reason().map(Enum::name).orElse("denied")));
        }
        return result;
    }

    public Component kickMessage(String playerName, ClientVersion client) {
        PlaceholderContext context = PlaceholderContext.of(playerName, client, config);
        return messages.kick(config, context);
    }

    public Component commandMessage(String template, PlaceholderContext context) {
        return messages.deserialize(template, context);
    }

    public boolean alreadyDecided(UUID uniqueId) {
        return outcomes.containsKey(uniqueId);
    }

    public ValidationResult previousOutcome(UUID uniqueId) {
        return outcomes.get(uniqueId);
    }

    public void remember(UUID uniqueId, ValidationResult result) {
        if (uniqueId != null && result != null) {
            outcomes.put(uniqueId, result);
        }
    }

    public void forget(UUID uniqueId) {
        if (uniqueId != null) {
            outcomes.remove(uniqueId);
        }
    }

    public void shutdown() {
        outcomes.clear();
    }
}
