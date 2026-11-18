package com.versionguard.command;

import net.kyori.adventure.text.Component;

/**
 * Platform-neutral command executor.
 */
public interface CommandAudience {

    String name();

    boolean hasPermission(String permission);

    void sendMessage(Component message);
}
