package com.versionguard.protocol;

import java.util.Optional;
import java.util.OptionalInt;

/**
 * Platform-neutral snapshot of ViaVersion's public protocol metadata.
 *
 * <p>This DTO exists so the common module never imports ViaVersion types.
 * Platforms populate it from {@code Via.getAPI()} and may leave it empty
 * when ViaVersion is not installed.</p>
 */
public record ViaVersionFacts(
        boolean available,
        boolean known,
        boolean snapshot,
        boolean experimental,
        OptionalInt protocol,
        Optional<String> name
) {

    public static ViaVersionFacts unavailable() {
        return new ViaVersionFacts(false, false, false, false, OptionalInt.empty(), Optional.empty());
    }

    public static ViaVersionFacts of(
            boolean known,
            boolean snapshot,
            boolean experimental,
            int protocol,
            String name
    ) {
        return new ViaVersionFacts(
                true,
                known,
                snapshot,
                experimental,
                protocol >= 0 ? OptionalInt.of(protocol) : OptionalInt.empty(),
                Optional.ofNullable(name)
        );
    }
}
