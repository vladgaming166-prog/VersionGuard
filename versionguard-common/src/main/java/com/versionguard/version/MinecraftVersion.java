package com.versionguard.version;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

/**
 * Parsed official Minecraft Java Edition release version.
 *
 * <p>Supports both the historic {@code 1.x.y} scheme and the calendar scheme
 * used from {@code 26.1} onward.</p>
 */
public final class MinecraftVersion implements Comparable<MinecraftVersion> {

    public static final Comparator<MinecraftVersion> COMPARATOR = Comparator
            .comparingInt(MinecraftVersion::major)
            .thenComparingInt(MinecraftVersion::minor)
            .thenComparingInt(MinecraftVersion::patch);

    private final int major;
    private final int minor;
    private final int patch;
    private final String original;

    private MinecraftVersion(int major, int minor, int patch, String original) {
        this.major = major;
        this.minor = minor;
        this.patch = patch;
        this.original = original;
    }

    public static MinecraftVersion of(int major, int minor, int patch) {
        return new MinecraftVersion(major, minor, patch, format(major, minor, patch));
    }

    public static Optional<MinecraftVersion> tryParse(String raw) {
        if (raw == null) {
            return Optional.empty();
        }
        String value = raw.trim();
        if (value.isEmpty()) {
            return Optional.empty();
        }
        String normalized = value.toLowerCase(Locale.ROOT);
        if (normalized.endsWith(".x")) {
            normalized = normalized.substring(0, normalized.length() - 2);
        }
        String[] parts = normalized.split("\\.");
        if (parts.length < 2 || parts.length > 3) {
            return Optional.empty();
        }
        try {
            int major = Integer.parseInt(parts[0]);
            int minor = Integer.parseInt(parts[1]);
            int patch = parts.length == 3 ? Integer.parseInt(parts[2]) : 0;
            if (major < 0 || minor < 0 || patch < 0) {
                return Optional.empty();
            }
            return Optional.of(new MinecraftVersion(major, minor, patch, value.trim()));
        } catch (NumberFormatException exception) {
            return Optional.empty();
        }
    }

    public static MinecraftVersion parse(String raw) {
        return tryParse(raw).orElseThrow(() ->
                new IllegalArgumentException("Invalid Minecraft version: " + raw));
    }

    public int major() {
        return major;
    }

    public int minor() {
        return minor;
    }

    public int patch() {
        return patch;
    }

    public String original() {
        return original;
    }

    public boolean isAtLeast(MinecraftVersion other) {
        return compareTo(other) >= 0;
    }

    public boolean isAtMost(MinecraftVersion other) {
        return compareTo(other) <= 0;
    }

    public boolean isBetween(MinecraftVersion minimum, MinecraftVersion maximum) {
        return isAtLeast(minimum) && isAtMost(maximum);
    }

    public String toCanonicalString() {
        return format(major, minor, patch);
    }

    @Override
    public int compareTo(MinecraftVersion other) {
        return COMPARATOR.compare(this, Objects.requireNonNull(other, "other"));
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof MinecraftVersion other)) {
            return false;
        }
        return major == other.major && minor == other.minor && patch == other.patch;
    }

    @Override
    public int hashCode() {
        return Objects.hash(major, minor, patch);
    }

    @Override
    public String toString() {
        return original == null || original.isBlank() ? toCanonicalString() : original;
    }

    public static Optional<MinecraftVersion> lowest(List<MinecraftVersion> versions) {
        return versions.stream().min(COMPARATOR);
    }

    public static Optional<MinecraftVersion> highest(List<MinecraftVersion> versions) {
        return versions.stream().max(COMPARATOR);
    }

    private static String format(int major, int minor, int patch) {
        if (patch == 0) {
            return major + "." + minor;
        }
        return major + "." + minor + "." + patch;
    }
}
