package com.versionguard.validation;

import com.versionguard.config.VersionGuardConfig;
import com.versionguard.protocol.ClientVersion;
import com.versionguard.protocol.OfficialProtocol;
import com.versionguard.protocol.OfficialProtocolCatalog;
import com.versionguard.protocol.ProtocolBits;

import java.util.Objects;

/**
 * Shared allowlist-based version restriction logic.
 *
 * <p>Official releases are accepted only when they appear in
 * {@link OfficialProtocolCatalog} and overlap the configured inclusive
 * version range. Snapshots, experimental clients, unknown protocols, and
 * future protocols above 26.3 are never accepted by default.</p>
 */
public final class VersionValidator {

    public ValidationResult validate(ClientVersion client, VersionGuardConfig config, boolean bypass) {
        Objects.requireNonNull(client, "client");
        Objects.requireNonNull(config, "config");

        if (bypass) {
            return new ValidationResult.Allowed(client, true);
        }

        if (client.snapshot() && config.blockSnapshots()) {
            return deny(client, DenialReason.SNAPSHOT);
        }

        if (client.experimental() && config.blockExperimental()) {
            return deny(client, DenialReason.EXPERIMENTAL);
        }

        if (client.protocol().isEmpty()) {
            return deny(client, DenialReason.DETECTION_FAILED);
        }

        int protocol = client.protocol().getAsInt();
        if (!ProtocolBits.isValidProtocolId(protocol)) {
            return deny(client, DenialReason.DETECTION_FAILED);
        }

        if (ProtocolBits.isSnapshotProtocol(protocol) && config.blockSnapshots()) {
            return deny(client, DenialReason.SNAPSHOT);
        }

        if (client.unknownProtocol() || client.officialProtocol().isEmpty()) {
            return denyUnmapped(client, config, protocol);
        }

        OfficialProtocol official = client.officialProtocol().orElseThrow();
        if (official.isEntirelyBelow(config.minimumVersion())) {
            return deny(client, DenialReason.BELOW_MINIMUM);
        }
        if (official.isEntirelyAbove(config.maximumVersion())) {
            return deny(client, DenialReason.ABOVE_MAXIMUM);
        }
        if (!official.overlaps(config.minimumVersion(), config.maximumVersion())) {
            return deny(client, DenialReason.UNKNOWN_PROTOCOL);
        }
        return new ValidationResult.Allowed(client, false);
    }

    private static ValidationResult denyUnmapped(ClientVersion client, VersionGuardConfig config, int protocol) {
        if (ProtocolBits.isSnapshotProtocol(protocol)) {
            return deny(client, DenialReason.SNAPSHOT);
        }
        if (client.experimental()) {
            return deny(client, DenialReason.EXPERIMENTAL);
        }
        if (protocol > config.maximumProtocol().protocol()
                || protocol > OfficialProtocolCatalog.highestKnownProtocol()) {
            return deny(client, DenialReason.ABOVE_MAXIMUM);
        }
        if (protocol < config.minimumProtocol().protocol()) {
            return deny(client, DenialReason.BELOW_MINIMUM);
        }
        if (config.blockUnknownProtocols() || client.unknownProtocol()) {
            return deny(client, DenialReason.UNKNOWN_PROTOCOL);
        }
        return deny(client, DenialReason.UNKNOWN_PROTOCOL);
    }

    private static ValidationResult deny(ClientVersion client, DenialReason reason) {
        return new ValidationResult.Denied(client, reason);
    }
}
