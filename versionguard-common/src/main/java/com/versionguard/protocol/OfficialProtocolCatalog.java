package com.versionguard.protocol;

import com.versionguard.version.MinecraftVersion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Optional;
import java.util.TreeMap;

/**
 * Allowlist of official Minecraft Java Edition release protocols.
 *
 * <p>Snapshots, pre-releases, release candidates, April Fools builds, and
 * other experimental clients are intentionally absent. A protocol that is
 * missing from this catalog is never treated as an allowed release.</p>
 */
public final class OfficialProtocolCatalog {

    private static final NavigableMap<Integer, OfficialProtocol> BY_PROTOCOL;
    private static final Map<MinecraftVersion, OfficialProtocol> BY_VERSION;

    static {
        NavigableMap<Integer, OfficialProtocol> byProtocol = new TreeMap<>();
        Map<MinecraftVersion, OfficialProtocol> byVersion = new LinkedHashMap<>();

        register(byProtocol, byVersion, 4, "1.7.2-1.7.5", range("1.7", 2, 5));
        register(byProtocol, byVersion, 5, "1.7.6-1.7.10", range("1.7", 6, 10));
        register(byProtocol, byVersion, 47, "1.8.x", range("1.8", 0, 9));
        register(byProtocol, byVersion, 107, "1.9", List.of("1.9"));
        register(byProtocol, byVersion, 108, "1.9.1", List.of("1.9.1"));
        register(byProtocol, byVersion, 109, "1.9.2", List.of("1.9.2"));
        register(byProtocol, byVersion, 110, "1.9.3-1.9.4", range("1.9", 3, 4));
        register(byProtocol, byVersion, 210, "1.10.x", range("1.10", 0, 2));
        register(byProtocol, byVersion, 315, "1.11", List.of("1.11"));
        register(byProtocol, byVersion, 316, "1.11.1-1.11.2", range("1.11", 1, 2));
        register(byProtocol, byVersion, 335, "1.12", List.of("1.12"));
        register(byProtocol, byVersion, 338, "1.12.1", List.of("1.12.1"));
        register(byProtocol, byVersion, 340, "1.12.2", List.of("1.12.2"));
        register(byProtocol, byVersion, 393, "1.13", List.of("1.13"));
        register(byProtocol, byVersion, 401, "1.13.1", List.of("1.13.1"));
        register(byProtocol, byVersion, 404, "1.13.2", List.of("1.13.2"));
        register(byProtocol, byVersion, 477, "1.14", List.of("1.14"));
        register(byProtocol, byVersion, 480, "1.14.1", List.of("1.14.1"));
        register(byProtocol, byVersion, 485, "1.14.2", List.of("1.14.2"));
        register(byProtocol, byVersion, 490, "1.14.3", List.of("1.14.3"));
        register(byProtocol, byVersion, 498, "1.14.4", List.of("1.14.4"));
        register(byProtocol, byVersion, 573, "1.15", List.of("1.15"));
        register(byProtocol, byVersion, 575, "1.15.1", List.of("1.15.1"));
        register(byProtocol, byVersion, 578, "1.15.2", List.of("1.15.2"));
        register(byProtocol, byVersion, 735, "1.16", List.of("1.16"));
        register(byProtocol, byVersion, 736, "1.16.1", List.of("1.16.1"));
        register(byProtocol, byVersion, 751, "1.16.2", List.of("1.16.2"));
        register(byProtocol, byVersion, 753, "1.16.3", List.of("1.16.3"));
        register(byProtocol, byVersion, 754, "1.16.4-1.16.5", range("1.16", 4, 5));
        register(byProtocol, byVersion, 755, "1.17", List.of("1.17"));
        register(byProtocol, byVersion, 756, "1.17.1", List.of("1.17.1"));
        register(byProtocol, byVersion, 757, "1.18-1.18.1", range("1.18", 0, 1));
        register(byProtocol, byVersion, 758, "1.18.2", List.of("1.18.2"));
        register(byProtocol, byVersion, 759, "1.19", List.of("1.19"));
        register(byProtocol, byVersion, 760, "1.19.1-1.19.2", range("1.19", 1, 2));
        register(byProtocol, byVersion, 761, "1.19.3", List.of("1.19.3"));
        register(byProtocol, byVersion, 762, "1.19.4", List.of("1.19.4"));
        register(byProtocol, byVersion, 763, "1.20-1.20.1", range("1.20", 0, 1));
        register(byProtocol, byVersion, 764, "1.20.2", List.of("1.20.2"));
        register(byProtocol, byVersion, 765, "1.20.3-1.20.4", range("1.20", 3, 4));
        register(byProtocol, byVersion, 766, "1.20.5-1.20.6", range("1.20", 5, 6));
        register(byProtocol, byVersion, 767, "1.21-1.21.1", range("1.21", 0, 1));
        register(byProtocol, byVersion, 768, "1.21.2-1.21.3", range("1.21", 2, 3));
        register(byProtocol, byVersion, 769, "1.21.4", List.of("1.21.4"));
        register(byProtocol, byVersion, 770, "1.21.5", List.of("1.21.5"));
        register(byProtocol, byVersion, 771, "1.21.6", List.of("1.21.6"));
        register(byProtocol, byVersion, 772, "1.21.7-1.21.8", range("1.21", 7, 8));
        register(byProtocol, byVersion, 773, "1.21.9-1.21.10", range("1.21", 9, 10));
        register(byProtocol, byVersion, 774, "1.21.11", List.of("1.21.11"));
        register(byProtocol, byVersion, 775, "26.1-26.1.2", List.of("26.1", "26.1.1", "26.1.2"));
        register(byProtocol, byVersion, 776, "26.2", List.of("26.2"));
        register(byProtocol, byVersion, 777, "26.3", List.of("26.3"));

        BY_PROTOCOL = Collections.unmodifiableNavigableMap(byProtocol);
        BY_VERSION = Collections.unmodifiableMap(byVersion);
    }

    private OfficialProtocolCatalog() {
    }

    public static Optional<OfficialProtocol> findByProtocol(int protocol) {
        if (ProtocolBits.isSnapshotProtocol(protocol)) {
            return Optional.empty();
        }
        return Optional.ofNullable(BY_PROTOCOL.get(protocol));
    }

    public static Optional<OfficialProtocol> findByVersion(MinecraftVersion version) {
        return Optional.ofNullable(BY_VERSION.get(version));
    }

    public static Optional<OfficialProtocol> findByVersionName(String version) {
        return MinecraftVersion.tryParse(version).flatMap(OfficialProtocolCatalog::findByVersion);
    }

    public static int lowestKnownProtocol() {
        return BY_PROTOCOL.firstKey();
    }

    public static int highestKnownProtocol() {
        return BY_PROTOCOL.lastKey();
    }

    public static OfficialProtocol latestRelease() {
        return BY_PROTOCOL.lastEntry().getValue();
    }

    public static boolean isOfficialReleaseProtocol(int protocol) {
        return findByProtocol(protocol).isPresent();
    }

    public static Map<Integer, OfficialProtocol> protocols() {
        return BY_PROTOCOL;
    }

    private static void register(
            NavigableMap<Integer, OfficialProtocol> byProtocol,
            Map<MinecraftVersion, OfficialProtocol> byVersion,
            int protocol,
            String displayName,
            List<String> versionNames
    ) {
        List<MinecraftVersion> releases = versionNames.stream()
                .map(MinecraftVersion::parse)
                .toList();
        MinecraftVersion lowest = MinecraftVersion.lowest(releases).orElseThrow();
        MinecraftVersion highest = MinecraftVersion.highest(releases).orElseThrow();
        OfficialProtocol entry = new OfficialProtocol(protocol, displayName, lowest, highest, releases);
        byProtocol.put(protocol, entry);
        for (MinecraftVersion release : releases) {
            byVersion.put(release, entry);
        }
    }

    private static List<String> range(String base, int fromInclusive, int toInclusive) {
        List<String> versions = new ArrayList<>();
        for (int index = fromInclusive; index <= toInclusive; index++) {
            if (index == 0) {
                versions.add(base);
            } else {
                versions.add(base + "." + index);
            }
        }
        return List.copyOf(versions);
    }
}
