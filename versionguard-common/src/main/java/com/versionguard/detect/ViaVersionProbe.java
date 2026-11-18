package com.versionguard.detect;

import com.versionguard.protocol.ViaVersionFacts;

import java.util.UUID;

/**
 * Optional ViaVersion lookup. Implementations must not throw if ViaVersion is absent.
 */
public interface ViaVersionProbe {

    boolean available();

    ViaVersionFacts probe(UUID uniqueId, Object platformPlayer);
}
