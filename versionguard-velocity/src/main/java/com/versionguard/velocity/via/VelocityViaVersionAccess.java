package com.versionguard.velocity.via;

import com.versionguard.detect.ViaVersionProbe;
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
            if (protocolVersion == null) {
                return ViaVersionFacts.unavailable();
            }
            boolean known = protocolVersion.isKnown() && protocolVersion != ProtocolVersion.unknown;
            boolean snapshot = protocolVersion.isSnapshot();
            boolean experimental = protocolVersion.getVersionType() == VersionType.SPECIAL;
            int protocol = protocolVersion.getOriginalVersion();
            return ViaVersionFacts.of(known, snapshot, experimental, protocol, protocolVersion.getName());
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
