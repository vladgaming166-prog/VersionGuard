package com.versionguard.message;

import com.versionguard.config.VersionGuardConfig;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Renders MiniMessage templates after placeholder substitution.
 */
public final class MessageService {

    private final MiniMessage miniMessage;

    public MessageService() {
        this(MiniMessage.miniMessage());
    }

    public MessageService(MiniMessage miniMessage) {
        this.miniMessage = Objects.requireNonNull(miniMessage, "miniMessage");
    }

    public Component kick(VersionGuardConfig config, PlaceholderContext context) {
        return joinLines(config.kickLines(), context);
    }

    public Component deserialize(String template, PlaceholderContext context) {
        String rendered = context.apply(template == null ? "" : template);
        if (rendered.isEmpty()) {
            return Component.empty();
        }
        try {
            return miniMessage.deserialize(rendered);
        } catch (RuntimeException exception) {
            return Component.text(rendered);
        }
    }

    public Component joinLines(List<String> lines, PlaceholderContext context) {
        if (lines == null || lines.isEmpty()) {
            return Component.empty();
        }
        List<Component> components = new ArrayList<>(lines.size());
        for (String line : lines) {
            components.add(deserialize(line, context));
        }
        return Component.join(JoinConfiguration.newlines(), components);
    }
}
