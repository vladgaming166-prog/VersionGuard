package com.versionguard.message;

import com.versionguard.config.VersionGuardConfig;
import com.versionguard.protocol.ClientVersion;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MessageServiceTest {

    @Test
    void replacesPlaceholdersInKickMessage() {
        VersionGuardConfig config = VersionGuardConfig.defaults();
        ClientVersion client = ClientVersion.ofProtocol(766);
        PlaceholderContext context = PlaceholderContext.of("Steve", client, config);
        String plain = PlainTextComponentSerializer.plainText().serialize(
                new MessageService().kick(config, context)
        );

        assertTrue(plain.contains("1.21"));
        assertTrue(plain.contains("26.3"));
        assertTrue(plain.contains(client.displayName()));
        assertFalse(plain.contains("%client_version%"));
        assertFalse(plain.contains("%player%"));
    }
}
