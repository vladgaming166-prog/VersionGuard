package com.versionguard.config;

import com.versionguard.version.MinecraftVersion;
import org.junit.jupiter.api.Test;

import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class YamlConfigLoaderTest {

    private final YamlConfigLoader loader = new YamlConfigLoader();

    @Test
    void loadsBundledDefaults() {
        VersionGuardConfig config = loader.defaultsFromClasspath();
        assertEquals(MinecraftVersion.parse("1.21"), config.minimumVersion());
        assertEquals(MinecraftVersion.parse("26.3"), config.maximumVersion());
        assertTrue(config.blockSnapshots());
        assertTrue(config.blockExperimental());
        assertTrue(config.blockUnknownProtocols());
        assertFalse(config.debug());
        assertEquals("versionguard.bypass", config.bypassPermission());
        assertEquals("versionguard.admin", config.adminPermission());
        assertTrue(config.kickLines().stream().anyMatch(line -> line.contains("%client_version%")));
    }

    @Test
    void rejectsUnknownConfiguredVersions() {
        String yaml = """
                settings:
                  minimum-version: "1.99"
                  maximum-version: "26.3"
                """;
        assertThrows(IllegalArgumentException.class, () -> loader.load(new StringReader(yaml)));
    }
}
