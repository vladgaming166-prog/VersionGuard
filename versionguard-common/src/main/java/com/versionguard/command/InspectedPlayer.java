package com.versionguard.command;

import com.versionguard.protocol.ClientVersion;

import java.util.UUID;

/**
 * Online player snapshot used by {@code /versionguard check}.
 */
public record InspectedPlayer(
        UUID uniqueId,
        String name,
        ClientVersion version,
        boolean bypass
) {
}
