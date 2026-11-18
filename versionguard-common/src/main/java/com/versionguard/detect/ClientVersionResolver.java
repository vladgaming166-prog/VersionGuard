package com.versionguard.detect;

import com.versionguard.protocol.ClientVersion;
import com.versionguard.protocol.ClientVersionFactory;
import com.versionguard.protocol.ProtocolBits;
import com.versionguard.protocol.ViaVersionFacts;

import java.util.OptionalInt;
import java.util.UUID;

/**
 * Combines a native handshake protocol with optional ViaVersion facts.
 */
public final class ClientVersionResolver {

    private final ViaVersionProbe viaVersionProbe;

    public ClientVersionResolver(ViaVersionProbe viaVersionProbe) {
        this.viaVersionProbe = viaVersionProbe;
    }

    public ClientVersion resolve(UUID uniqueId, Object platformPlayer, int nativeProtocol) {
        ViaVersionFacts via = safeViaFacts(uniqueId, platformPlayer);
        if (!ProtocolBits.isValidProtocolId(nativeProtocol) && via.protocol().isPresent()) {
            return ClientVersionFactory.fromNativeProtocol(via.protocol().getAsInt(), via);
        }
        return ClientVersionFactory.fromNativeProtocol(nativeProtocol, via);
    }

    public ClientVersion resolve(UUID uniqueId, Object platformPlayer, OptionalInt nativeProtocol) {
        int protocol = nativeProtocol.orElse(ProtocolBits.UNKNOWN_PROTOCOL);
        return resolve(uniqueId, platformPlayer, protocol);
    }

    private ViaVersionFacts safeViaFacts(UUID uniqueId, Object platformPlayer) {
        if (viaVersionProbe == null || !viaVersionProbe.available()) {
            return ViaVersionFacts.unavailable();
        }
        try {
            return viaVersionProbe.probe(uniqueId, platformPlayer);
        } catch (RuntimeException exception) {
            return ViaVersionFacts.unavailable();
        }
    }
}
