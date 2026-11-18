package com.versionguard.protocol;

import com.versionguard.version.MinecraftVersion;

import java.util.List;
import java.util.Objects;

/**
 * A single official Minecraft Java Edition protocol and the releases that share it.
 */
public record OfficialProtocol(
        int protocol,
        String displayName,
        MinecraftVersion lowestRelease,
        MinecraftVersion highestRelease,
        List<MinecraftVersion> releases
) {

    public OfficialProtocol {
        Objects.requireNonNull(displayName, "displayName");
        Objects.requireNonNull(lowestRelease, "lowestRelease");
        Objects.requireNonNull(highestRelease, "highestRelease");
        releases = List.copyOf(Objects.requireNonNull(releases, "releases"));
        if (releases.isEmpty()) {
            throw new IllegalArgumentException("Official protocol " + protocol + " has no releases");
        }
    }

    public boolean overlaps(MinecraftVersion minimum, MinecraftVersion maximum) {
        return highestRelease.isAtLeast(minimum) && lowestRelease.isAtMost(maximum);
    }

    public boolean isEntirelyBelow(MinecraftVersion minimum) {
        return highestRelease.compareTo(minimum) < 0;
    }

    public boolean isEntirelyAbove(MinecraftVersion maximum) {
        return lowestRelease.compareTo(maximum) > 0;
    }
}
