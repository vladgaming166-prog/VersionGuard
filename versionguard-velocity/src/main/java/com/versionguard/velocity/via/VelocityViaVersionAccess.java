package com.versionguard.velocity.via;

import com.versionguard.detect.ViaVersionProbe;
import com.versionguard.protocol.ProtocolBits;
import com.versionguard.protocol.ViaVersionFacts;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.ViaAPI;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viaversion.api.protocol.version.VersionType;

import java.util.UUID;

/**
 * Isolated ViaVersion API access for Velocity.
 */
final class VelocityViaVersionAccess implements ViaVersionProbe {

    @Override
    public boolean available() {
        try {
            return Via.getAPI() != null;
        } catch (Throwable throwable) {
            return false;
        }
    }

    @Override
    public ViaVersionFacts probe(UUID uniqueId, Object platformPlayer) {
        try {
            ViaAPI<?> api = Via.getAPI();
            if (api == null) {
                return ViaVersionFacts.unavailable();
            }
            ProtocolVersion protocolVersion = resolve(api, uniqueId, platformPlayer);
            if (protocolVersion == null || protocolVersion == ProtocolVersion.unknown || !protocolVersion.isKnown()) {
                return ViaVersionFacts.unavailable();
            }
            int protocol = protocolVersion.getVersion();
            boolean snapshot = protocolVersion.isSnapshot() && ProtocolBits.isSnapshotProtocol(protocolVersion.getOriginalVersion())
                    && ProtocolBits.isSnapshotProtocol(protocol);
            boolean experimental = protocolVersion.getVersionType() == VersionType.SPECIAL;
            return ViaVersionFacts.of(true, snapshot, experimental, protocol, protocolVersion.getName());
        } catch (NoSuchMethodError | NoClassDefFoundError compatibility) {
            return fallbackLegacy(uniqueId, platformPlayer);
        } catch (Throwable throwable) {
            return ViaVersionFacts.unavailable();
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static ProtocolVersion resolve(ViaAPI api, UUID uniqueId, Object platformPlayer) {
        if (platformPlayer != null) {
            try {
                ProtocolVersion fromPlayer = api.getPlayerProtocolVersion(platformPlayer);
                if (fromPlayer != null && fromPlayer != ProtocolVersion.unknown) {
                    return fromPlayer;
                }
            } catch (RuntimeException ignored) {
                // Fall through to UUID lookup.
            }
        }
        if (uniqueId != null) {
            return api.getPlayerProtocolVersion(uniqueId);
        }
        return ProtocolVersion.unknown;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static ViaVersionFacts fallbackLegacy(UUID uniqueId, Object platformPlayer) {
        try {
            ViaAPI api = Via.getAPI();
            int protocol = -1;
            if (platformPlayer != null) {
                protocol = api.getPlayerVersion(platformPlayer);
            }
            if (protocol < 0 && uniqueId != null) {
                protocol = api.getPlayerVersion(uniqueId);
            }
            if (protocol < 0) {
                return ViaVersionFacts.unavailable();
            }
            return ViaVersionFacts.of(true, false, false, protocol, "protocol " + protocol);
        } catch (Throwable throwable) {
            return ViaVersionFacts.unavailable();
        }
    }
}
