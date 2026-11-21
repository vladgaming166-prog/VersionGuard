package com.versionguard.validation;

import com.versionguard.config.VersionGuardConfig;
import com.versionguard.protocol.ClientVersion;
import com.versionguard.protocol.ClientVersionFactory;
import com.versionguard.protocol.ProtocolBits;
import com.versionguard.protocol.ViaVersionFacts;
import com.versionguard.version.MinecraftVersion;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VersionValidatorTest {

    private final VersionValidator validator = new VersionValidator();
    private final VersionGuardConfig defaults = VersionGuardConfig.defaults();

    @Test
    void allowsOfficialMinimumAndMaximum() {
        assertAllowed(validator.validate(ClientVersion.ofProtocol(767), defaults, false));
        assertAllowed(validator.validate(ClientVersion.ofProtocol(774), defaults, false));
        assertAllowed(validator.validate(ClientVersion.ofProtocol(775), defaults, false));
        assertAllowed(validator.validate(ClientVersion.ofProtocol(776), defaults, false));
        assertAllowed(validator.validate(ClientVersion.ofProtocol(777), defaults, false));
    }

    @Test
    void blocksOlderOfficialReleasesIncludingShared1205Protocol() {
        ValidationResult result = validator.validate(ClientVersion.ofProtocol(766), defaults, false);
        assertDenied(result, DenialReason.BELOW_MINIMUM);
        assertDenied(validator.validate(ClientVersion.ofProtocol(765), defaults, false), DenialReason.BELOW_MINIMUM);
        assertDenied(validator.validate(ClientVersion.ofProtocol(47), defaults, false), DenialReason.BELOW_MINIMUM);
    }

    @Test
    void blocksFutureProtocolsAboveMaximum() {
        assertDenied(validator.validate(ClientVersion.ofProtocol(778), defaults, false), DenialReason.ABOVE_MAXIMUM);
        assertDenied(validator.validate(ClientVersion.ofProtocol(800), defaults, false), DenialReason.ABOVE_MAXIMUM);
    }

    @Test
    void blocksSnapshotsEvenWhenCloseToAnAllowedRelease() {
        ClientVersion snapshot = ClientVersionFactory.fromNativeProtocol(
                0x40000152,
                ViaVersionFacts.of(true, true, false, 0x40000152, "26.3-rc3")
        );
        assertTrue(snapshot.snapshot());
        assertDenied(validator.validate(snapshot, defaults, false), DenialReason.SNAPSHOT);

        ClientVersion nativeSnapshot = ClientVersion.ofProtocol(ProtocolBits.SNAPSHOT_INDICATOR_BIT | 338);
        assertDenied(validator.validate(nativeSnapshot, defaults, false), DenialReason.SNAPSHOT);
    }

    @Test
    void blocksExperimentalAndUnknownProtocols() {
        ClientVersion experimental = ClientVersionFactory.fromNativeProtocol(
                23,
                ViaVersionFacts.of(true, false, true, 23, "24w14potato")
        );
        assertDenied(validator.validate(experimental, defaults, false), DenialReason.EXPERIMENTAL);
        assertDenied(validator.validate(ClientVersion.unknownClient(), defaults, false), DenialReason.DETECTION_FAILED);
        assertDenied(validator.validate(ClientVersion.ofProtocol(-1), defaults, false), DenialReason.DETECTION_FAILED);
    }

    @Test
    void bypassPermissionAllowsUnsupportedClients() {
        ValidationResult result = validator.validate(ClientVersion.ofProtocol(47), defaults, true);
        assertAllowed(result);
        assertTrue(((ValidationResult.Allowed) result).bypassed());
    }

    @Test
    void respectsCustomMinimumAndMaximum() {
        VersionGuardConfig narrowed = VersionGuardConfig.builder()
                .minimumVersion(MinecraftVersion.parse("1.21.4"))
                .maximumVersion(MinecraftVersion.parse("26.1"))
                .build();

        assertDenied(validator.validate(ClientVersion.ofProtocol(767), narrowed, false), DenialReason.BELOW_MINIMUM);
        assertAllowed(validator.validate(ClientVersion.ofProtocol(769), narrowed, false));
        assertAllowed(validator.validate(ClientVersion.ofProtocol(775), narrowed, false));
        assertDenied(validator.validate(ClientVersion.ofProtocol(776), narrowed, false), DenialReason.ABOVE_MAXIMUM);
        assertDenied(validator.validate(ClientVersion.ofProtocol(777), narrowed, false), DenialReason.ABOVE_MAXIMUM);
    }

    @Test
    void allowsOfficialClientsWhenSnapshotAndExperimentalFlagsAreEnabled() {
        ClientVersion poisonedByUnknownVia = ClientVersionFactory.fromNativeProtocol(
                767,
                ViaVersionFacts.of(false, true, true, -1, "UNKNOWN")
        );
        assertAllowed(validator.validate(poisonedByUnknownVia, defaults, false));
        assertAllowed(validator.validate(ClientVersion.ofProtocol(777), defaults, false));
    }

    @Test
    void neverAllowsUnknownProtocolsInsideTheNumericRange() {
        // 767-777 are all assigned official releases; a non-catalog protocol still fails closed.
        ClientVersion unknownInRange = new ClientVersion(
                java.util.OptionalInt.of(770),
                "mystery",
                false,
                false,
                true,
                java.util.Optional.empty(),
                java.util.Optional.empty(),
                java.util.Optional.empty()
        );
        assertDenied(validator.validate(unknownInRange, defaults, false), DenialReason.UNKNOWN_PROTOCOL);
    }

    private static void assertAllowed(ValidationResult result) {
        assertTrue(result.allowed(), () -> "Expected allow but got " + result);
        assertInstanceOf(ValidationResult.Allowed.class, result);
    }

    private static void assertDenied(ValidationResult result, DenialReason reason) {
        assertFalse(result.allowed(), () -> "Expected deny but got " + result);
        assertEquals(reason, result.reason().orElseThrow());
    }
}
