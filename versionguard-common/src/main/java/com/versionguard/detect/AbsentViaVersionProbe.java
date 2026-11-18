package com.versionguard.detect;

import com.versionguard.protocol.ViaVersionFacts;

import java.util.UUID;

/**
 * Used when ViaVersion is not installed.
 */
public final class AbsentViaVersionProbe implements ViaVersionProbe {

    @Override
    public boolean available() {
        return false;
    }

    @Override
    public ViaVersionFacts probe(UUID uniqueId, Object platformPlayer) {
        return ViaVersionFacts.unavailable();
    }
}
