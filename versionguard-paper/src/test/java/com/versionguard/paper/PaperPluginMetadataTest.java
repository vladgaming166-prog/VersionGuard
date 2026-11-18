package com.versionguard.paper;

import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PaperPluginMetadataTest {

    @Test
    void pluginYmlDeclaresPaperEntryPointAndPermissions() {
        InputStream stream = VersionGuardPaperPlugin.class.getResourceAsStream("/plugin.yml");
        assertNotNull(stream, "plugin.yml must be on the Paper classpath");
        Map<String, Object> yaml = new Yaml().load(stream);
        assertEquals("VersionGuard", yaml.get("name"));
        assertEquals("com.versionguard.paper.VersionGuardPaperPlugin", yaml.get("main"));
        assertEquals("1.21", String.valueOf(yaml.get("api-version")));
        assertTrue(String.valueOf(yaml.get("softdepend")).toLowerCase().contains("viaversion"));

        @SuppressWarnings("unchecked")
        Map<String, Object> permissions = (Map<String, Object>) yaml.get("permissions");
        assertTrue(permissions.containsKey("versionguard.admin"));
        assertTrue(permissions.containsKey("versionguard.bypass"));
    }
}
