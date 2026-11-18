package com.versionguard.protocol;

/**
 * Minecraft Java protocol helpers.
 *
 * <p>Since 1.16.4-pre1, development releases set bit 30 ({@code 0x40000000})
 * so they cannot be mistaken for the corresponding official release.</p>
 */
public final class ProtocolBits {

    public static final int SNAPSHOT_INDICATOR_BIT = 1 << 30;
    public static final int UNKNOWN_PROTOCOL = -1;

    private ProtocolBits() {
    }

    public static boolean isValidProtocolId(int protocol) {
        return protocol >= 0;
    }

    public static boolean isSnapshotProtocol(int protocol) {
        return isValidProtocolId(protocol) && (protocol & SNAPSHOT_INDICATOR_BIT) != 0;
    }

    public static int stripSnapshotBit(int protocol) {
        return protocol & ~SNAPSHOT_INDICATOR_BIT;
    }
}
