package com.versionguard.protocol;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProtocolBitsTest {

    @Test
    void detectsSnapshotBit() {
        assertFalse(ProtocolBits.isSnapshotProtocol(767));
        assertFalse(ProtocolBits.isSnapshotProtocol(777));
        assertTrue(ProtocolBits.isSnapshotProtocol(0x40000152));
        assertTrue(ProtocolBits.isSnapshotProtocol(ProtocolBits.SNAPSHOT_INDICATOR_BIT | 1));
        assertFalse(ProtocolBits.isSnapshotProtocol(-1));
    }

    @Test
    void stripsSnapshotBitWithoutChangingReleases() {
        assertEquals(767, ProtocolBits.stripSnapshotBit(767));
        assertEquals(0x152, ProtocolBits.stripSnapshotBit(0x40000152));
    }
}
