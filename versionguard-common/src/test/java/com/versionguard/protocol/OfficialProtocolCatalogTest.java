package com.versionguard.protocol;

import com.versionguard.version.MinecraftVersion;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OfficialProtocolCatalogTest {

    @Test
    void mapsRequiredOfficialReleases() {
        assertEquals(767, OfficialProtocolCatalog.findByVersionName("1.21").orElseThrow().protocol());
        assertEquals(767, OfficialProtocolCatalog.findByVersionName("1.21.1").orElseThrow().protocol());
        assertEquals(774, OfficialProtocolCatalog.findByVersionName("1.21.11").orElseThrow().protocol());
        assertEquals(775, OfficialProtocolCatalog.findByVersionName("26.1").orElseThrow().protocol());
        assertEquals(775, OfficialProtocolCatalog.findByVersionName("26.1.2").orElseThrow().protocol());
        assertEquals(776, OfficialProtocolCatalog.findByVersionName("26.2").orElseThrow().protocol());
        assertEquals(777, OfficialProtocolCatalog.findByVersionName("26.3").orElseThrow().protocol());
    }

    @Test
    void mapsSharedLegacyProtocols() {
        assertEquals(766, OfficialProtocolCatalog.findByProtocol(766).orElseThrow().protocol());
        assertEquals(MinecraftVersion.parse("1.20.5"), OfficialProtocolCatalog.findByProtocol(766).orElseThrow().lowestRelease());
        assertEquals(MinecraftVersion.parse("1.20.6"), OfficialProtocolCatalog.findByProtocol(766).orElseThrow().highestRelease());
    }

    @Test
    void neverTreatsSnapshotsAsOfficialReleases() {
        assertTrue(OfficialProtocolCatalog.findByProtocol(0x40000152).isEmpty());
        assertTrue(OfficialProtocolCatalog.findByProtocol(ProtocolBits.SNAPSHOT_INDICATOR_BIT | 777).isEmpty());
    }

    @Test
    void highestKnownReleaseIsMinecraft263() {
        assertEquals(777, OfficialProtocolCatalog.highestKnownProtocol());
        assertEquals("26.3", OfficialProtocolCatalog.latestRelease().displayName());
    }
}
