# VersionGuard

VersionGuard is a lightweight Minecraft client version restriction plugin for **Paper**, **Purpur**, and **Velocity**.

It allows players to connect only with official Minecraft Java Edition releases from **1.21** through **26.3**. Snapshots, pre-releases, release candidates, experimental builds, unknown protocols, and future versions above 26.3 are denied.

The project is a Maven multi-module build with shared validation logic and separate platform plugins:

| Artifact | Platform |
| --- | --- |
| `VersionGuard-Paper.jar` | Paper and Purpur |
| `VersionGuard-Velocity.jar` | Velocity |

## Features

- Allowlist of official Java Edition release protocols from 1.21 through 26.3
- Snapshot detection using Minecraft's protocol snapshot bit (`0x40000000`)
- Optional ViaVersion integration for snapshot, experimental, and named version metadata
- Fail-closed behaviour when a protocol cannot be identified
- MiniMessage kick messages with placeholders
- `/versionguard` administration commands on Paper/Purpur and Velocity
- Bypass permission that never applies unless explicitly granted

## Requirements

- Java 21 or newer
- Paper 1.21+ / Purpur 1.21+ **or** Velocity 3.4+
- Optional but **strongly recommended**: [ViaVersion](https://modrinth.com/plugin/viaversion)

VersionGuard compiles against Java 21 and the public Paper, Velocity, and ViaVersion APIs. It does not use NMS or unofficial internals.

## Installation

### Paper / Purpur

1. Build or download `VersionGuard-Paper.jar`.
2. Place the JAR in the server `plugins/` folder.
3. Restart the server.
4. Edit `plugins/VersionGuard/config.yml` if you need a narrower official range.
5. Install ViaVersion in the same `plugins/` folder for the most accurate client identification.

### Velocity

1. Build or download `VersionGuard-Velocity.jar`.
2. Place the JAR in the proxy `plugins/` folder.
3. Restart the proxy.
4. Edit `plugins/versionguard/config.yml`.
5. Install ViaVersion on the **proxy** so clients are identified before they reach a backend server.

Use only one VersionGuard platform JAR per process. On a Velocity network, install the Velocity JAR on the proxy. Installing the Paper JAR on backend servers as well is optional extra defence, not a replacement for the proxy check.

### ViaVersion

ViaVersion is optional. If it is missing, VersionGuard still enforces restrictions using the native handshake protocol and will never treat a snapshot bit as an official release.

When ViaVersion is installed, VersionGuard uses only the public API:

- `Via.getAPI().getPlayerProtocolVersion(...)`
- `ProtocolVersion.isSnapshot()`
- `ProtocolVersion.getVersionType()`

If ViaVersion is absent or its API is not ready, VersionGuard logs a clear warning and continues with native detection. It does not crash.

Recommended install: https://modrinth.com/plugin/viaversion

## Allowed and blocked versions

**Allowed by default**

- Minecraft 1.21 and every official Java Edition release after it
- Minecraft 26.3

**Blocked by default**

- Minecraft 1.20.6 and older, including 1.20.5 (protocol 766 is shared by 1.20.5 and 1.20.6; both are below 1.21)
- Snapshots, pre-releases, and release candidates
- Experimental and April Fools clients
- Unknown protocols
- Future protocols above 26.3

VersionGuard does **not** allow a client just because its numeric protocol falls between 767 and 777. The protocol must map to a known official release and must not have the snapshot indicator bit set.

Some official patches share a protocol. VersionGuard cannot distinguish those clients from each other:

| Protocol | Official releases |
| ---: | --- |
| 766 | 1.20.5, 1.20.6 |
| 767 | 1.21, 1.21.1 |
| 768 | 1.21.2, 1.21.3 |
| 769 | 1.21.4 |
| 770 | 1.21.5 |
| 771 | 1.21.6 |
| 772 | 1.21.7, 1.21.8 |
| 773 | 1.21.9, 1.21.10 |
| 774 | 1.21.11 |
| 775 | 26.1, 26.1.1, 26.1.2 |
| 776 | 26.2 |
| 777 | 26.3 |

## Configuration

`config.yml` is generated on first start.

```yaml
settings:
  minimum-version: "1.21"
  maximum-version: "26.3"

  block-snapshots: true
  block-experimental: true
  block-unknown-protocols: true

  debug: false

messages:
  kick:
    - "<red><bold>VersionGuard</bold></red>"
    - ""
    - "<gray>Your Minecraft version is not supported.</gray>"
    - ""
    - "<white>Minimum version: <green>1.21</green></white>"
    - "<white>Maximum version: <green>26.3</green></white>"
    - ""
    - "<gray>Your version: <red>%client_version%</red></gray>"
    - ""
    - "<yellow>Please update your Minecraft client.</yellow>"

permissions:
  bypass: "versionguard.bypass"
  admin: "versionguard.admin"
```

`minimum-version` and `maximum-version` must be official mapped releases. Invalid values are rejected on load/reload; a failed reload keeps the previous configuration.

If you change the allowed range, update the kick message lines so players see the same versions.

### Placeholders

Kick and command messages support MiniMessage plus these placeholders:

| Placeholder | Value |
| --- | --- |
| `%player%` | Player name |
| `%client_version%` | Detected client version name |
| `%client_protocol%` | Numeric protocol, or `unknown` |
| `%minimum_version%` | Configured minimum |
| `%maximum_version%` | Configured maximum |

## Commands

Permission: `versionguard.admin`

| Command | Description |
| --- | --- |
| `/versionguard reload` | Reloads `config.yml` |
| `/versionguard info` | Shows platform, range, flags, and ViaVersion status |
| `/versionguard check <player>` | Shows an online player's detected version and decision |

Alias: `/vg`

## Permissions

| Permission | Default | Description |
| --- | --- | --- |
| `versionguard.admin` | op | Administration commands |
| `versionguard.bypass` | false | Join with an otherwise unsupported version |
| `versionguard.*` | false | Both of the above |

Operators do **not** receive `versionguard.bypass` automatically. Bypass is granted only by that permission node.

## Build

```bash
mvn -DskipTests=false clean package
```

The build writes:

- `versionguard-paper/target/VersionGuard-Paper.jar`
- `versionguard-velocity/target/VersionGuard-Velocity.jar`

Do not use the unshaded `versionguard-common` JAR on a server.

## Project structure

```
VersionGuard/
├── pom.xml
├── versionguard-common/      # validation, protocol catalog, config, messages
├── versionguard-paper/       # Paper/Purpur plugin
├── versionguard-velocity/    # Velocity plugin
└── README.md
```

Shared business logic lives in `versionguard-common`. Platform modules only load configuration, listen to connection events, register commands, and optionally hook ViaVersion.

## Detection order

1. Native handshake protocol from Paper (`Player#getProtocolVersion()`) or Velocity (`Player#getProtocolVersion().getProtocol()`)
2. ViaVersion public API, when installed
3. Official protocol catalog
4. Deny unless the client is a mapped official release inside the configured range, or the player has `versionguard.bypass`

Paper checks during `PlayerLoginEvent` and re-checks on join so ViaVersion metadata can still reject a snapshot that was not identifiable at login. Velocity checks during `LoginEvent` and again during `ServerPreConnectEvent`, so unauthorized clients never enter a backend server.

## License

You may use, modify, and distribute VersionGuard with your server network.
