package com.versionguard.protocol;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ClientVersionFactoryTest {

    @Test
    void nativeSnapshotBitWinsOverReleaseLookingViaName() {
        ClientVersion version = ClientVersionFactory.fromNativeProtocol(
                0x40000152,
                ViaVersionFacts.of(true, false, false, 777, "26.3")
        );
        assertTrue(version.snapshot());
        assertFalse(version.isOfficialRelease());
        assertTrue(version.officialProtocol().isEmpty());
    }

    @Test
    void viaExperimentalMarksSpecialClients() {
        ClientVersion version = ClientVersionFactory.fromNativeProtocol(
                23,
                ViaVersionFacts.of(true, false, true, 23, "24w14potato")
        );
        assertTrue(version.experimental());
        assertEquals("24w14potato", version.displayName());
        assertFalse(version.isOfficialRelease());
    }

    @Test
    void officialReleaseKeepsCatalogNameWhenViaIsMissing() {
        ClientVersion version = ClientVersion.ofProtocol(767);
        assertTrue(version.isOfficialRelease());
        assertEquals("1.21-1.21.1", version.displayName());
        assertEquals(767, version.protocol().orElseThrow());
    }

    @Test
    void unknownViaVersionSpecialTypeDoesNotMarkOfficialClientsExperimental() {
        ClientVersion version = ClientVersionFactory.fromNativeProtocol(
                777,
                ViaVersionFacts.of(false, false, true, -1, "UNKNOWN")
        );
        assertFalse(version.experimental());
        assertFalse(version.snapshot());
        assertTrue(version.isOfficialRelease());
        assertEquals(777, version.protocol().orElseThrow());
    }

    @Test
    void viaSnapshotFlagOnMappedReleaseDoesNotOverrideOfficialHandshake() {
        ClientVersion version = ClientVersionFactory.fromNativeProtocol(
                775,
                ViaVersionFacts.of(true, true, false, 775, "26.1")
        );
        assertFalse(version.snapshot());
        assertTrue(version.isOfficialRelease());
    }
}
