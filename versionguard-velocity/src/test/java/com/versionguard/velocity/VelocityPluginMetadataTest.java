package com.versionguard.velocity;

import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VelocityPluginMetadataTest {

    @Test
    void velocityPluginJsonDeclaresProxyEntryPoint() throws Exception {
        InputStream stream = VersionGuardVelocityPlugin.class.getResourceAsStream("/velocity-plugin.json");
        assertNotNull(stream, "velocity-plugin.json must be on the Velocity classpath");
        String json = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
        assertTrue(json.contains("\"id\":\"versionguard\"") || json.contains("\"id\": \"versionguard\""));
        assertTrue(json.contains("com.versionguard.velocity.VersionGuardVelocityPlugin"));
        assertTrue(json.toLowerCase().contains("viaversion"));
    }
}
