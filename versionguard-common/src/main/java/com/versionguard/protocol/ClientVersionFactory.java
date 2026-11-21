package com.versionguard.protocol;

import com.versionguard.version.MinecraftVersion;

import java.util.Optional;
import java.util.OptionalInt;

/**
 * Merges native handshake protocol data with optional ViaVersion metadata.
 *
 * <p>ViaVersion is used to refine identity (snapshot, experimental, name) but
 * never to override a native snapshot bit or to invent an official release
 * that is missing from {@link OfficialProtocolCatalog}.</p>
 */
public final class ClientVersionFactory {

    private ClientVersionFactory() {
    }

    public static ClientVersion fromNativeProtocol(int protocol, ViaVersionFacts via) {
        if (!ProtocolBits.isValidProtocolId(protocol)) {
            if (via.available() && via.protocol().isPresent()) {
                return fromDetected(via.protocol().getAsInt(), via);
            }
            if (via.available() && via.name().isPresent()) {
                return ClientVersion.unknownClient(via.name().get());
            }
            return ClientVersion.unknownClient();
        }
        return fromDetected(protocol, via);
    }

    public static ClientVersion fromDetected(int protocol, ViaVersionFacts via) {
        Optional<OfficialProtocol> official = ProtocolBits.isSnapshotProtocol(protocol)
                ? Optional.empty()
                : OfficialProtocolCatalog.findByProtocol(protocol);

        // Native handshake is the source of truth for official releases. ViaVersion's
        // ProtocolVersion.unknown is VersionType.SPECIAL, and some ViaVersion builds
        // mark release objects as snapshots because they store a mapped snapshot id.
        // Those flags must not reject a known official handshake protocol.
        boolean snapshot = ProtocolBits.isSnapshotProtocol(protocol)
                || (official.isEmpty() && via.known() && viaReportsSnapshotProtocol(via));
        boolean experimental = official.isEmpty() && via.known() && via.experimental();
        if (snapshot) {
            official = Optional.empty();
        }

        String displayName = resolveDisplayName(protocol, snapshot, experimental, official, via);
        boolean unknown = !snapshot && !experimental && official.isEmpty();

        if (via.available() && !via.known() && official.isEmpty()) {
            unknown = true;
        }

        return new ClientVersion(
                OptionalInt.of(protocol),
                displayName,
                snapshot,
                experimental,
                unknown,
                official,
                official.map(OfficialProtocol::lowestRelease),
                official.map(OfficialProtocol::highestRelease)
        );
    }

    private static String resolveDisplayName(
            int protocol,
            boolean snapshot,
            boolean experimental,
            Optional<OfficialProtocol> official,
            ViaVersionFacts via
    ) {
        if (via.name().isPresent()) {
            String viaName = via.name().get();
            if (!viaName.isBlank() && !viaName.equalsIgnoreCase("UNKNOWN")) {
                return viaName;
            }
        }
        if (official.isPresent()) {
            return official.get().displayName();
        }
        if (snapshot) {
            return "snapshot (" + protocol + ")";
        }
        if (experimental) {
            return "experimental (" + protocol + ")";
        }
        MinecraftVersion parsedFromVia = via.name().flatMap(MinecraftVersion::tryParse).orElse(null);
        if (parsedFromVia != null) {
            return parsedFromVia.toCanonicalString();
        }
        return "Unknown (" + protocol + ")";
    }

    private static boolean viaReportsSnapshotProtocol(ViaVersionFacts via) {
        return via.protocol().isPresent() && ProtocolBits.isSnapshotProtocol(via.protocol().getAsInt());
    }
}
