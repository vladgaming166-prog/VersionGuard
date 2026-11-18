package com.versionguard.protocol;

import com.versionguard.version.MinecraftVersion;

import java.util.Optional;
import java.util.OptionalInt;

/**
 * Detected Minecraft client identity used for validation and messages.
 */
public record ClientVersion(
        OptionalInt protocol,
        String displayName,
        boolean snapshot,
        boolean experimental,
        boolean unknownProtocol,
        Optional<OfficialProtocol> officialProtocol,
        Optional<MinecraftVersion> lowestEquivalentRelease,
        Optional<MinecraftVersion> highestEquivalentRelease
) {

    public static ClientVersion unknownClient() {
        return unknownClient("unknown");
    }

    public static ClientVersion unknownClient(String displayName) {
        return new ClientVersion(
                OptionalInt.empty(),
                displayName == null || displayName.isBlank() ? "unknown" : displayName,
                false,
                false,
                true,
                Optional.empty(),
                Optional.empty(),
                Optional.empty()
        );
    }

    public static ClientVersion ofProtocol(int protocol) {
        return ClientVersionFactory.fromNativeProtocol(protocol, ViaVersionFacts.unavailable());
    }

    public String protocolLabel() {
        return protocol.isPresent() ? Integer.toString(protocol.getAsInt()) : "unknown";
    }

    public boolean isOfficialRelease() {
        return !unknownProtocol && !snapshot && !experimental && officialProtocol.isPresent();
    }
}
