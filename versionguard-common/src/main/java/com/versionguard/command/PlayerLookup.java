package com.versionguard.command;

import java.util.List;
import java.util.Optional;

/**
 * Resolves online players for commands.
 */
public interface PlayerLookup {

    Optional<InspectedPlayer> find(String name);

    List<String> onlineNames();
}
