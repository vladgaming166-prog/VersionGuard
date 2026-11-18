package com.versionguard.validation;

import com.versionguard.protocol.ClientVersion;

import java.util.Objects;
import java.util.Optional;

/**
 * Outcome of a version restriction check.
 */
public sealed interface ValidationResult permits ValidationResult.Allowed, ValidationResult.Denied {

    boolean allowed();

    ClientVersion client();

    Optional<DenialReason> reason();

    record Allowed(ClientVersion client, boolean bypassed) implements ValidationResult {
        public Allowed {
            Objects.requireNonNull(client, "client");
        }

        @Override
        public boolean allowed() {
            return true;
        }

        @Override
        public Optional<DenialReason> reason() {
            return Optional.empty();
        }
    }

    record Denied(ClientVersion client, DenialReason denialReason) implements ValidationResult {
        public Denied {
            Objects.requireNonNull(client, "client");
            Objects.requireNonNull(denialReason, "denialReason");
        }

        @Override
        public boolean allowed() {
            return false;
        }

        @Override
        public Optional<DenialReason> reason() {
            return Optional.of(denialReason);
        }
    }
}
