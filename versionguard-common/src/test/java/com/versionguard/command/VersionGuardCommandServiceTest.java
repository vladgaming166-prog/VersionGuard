package com.versionguard.command;

import com.versionguard.platform.VersionGuardRuntime;
import com.versionguard.protocol.ClientVersion;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VersionGuardCommandServiceTest {

    @TempDir
    Path tempDir;

    @Test
    void reloadAndCheckUseSharedLogic() throws Exception {
        VersionGuardRuntime runtime = new VersionGuardRuntime(tempDir, new RecordingLogger(), "test");
        runtime.load();

        InspectedPlayer player = new InspectedPlayer(
                UUID.randomUUID(),
                "Steve",
                ClientVersion.ofProtocol(777),
                false
        );
        VersionGuardCommandService service = new VersionGuardCommandService(runtime, new StubLookup(player));

        RecordingAudience admin = new RecordingAudience("Console", true);
        service.execute(admin, new String[]{"info"});
        service.execute(admin, new String[]{"check", "Steve"});

        String combined = String.join("\n", admin.plainMessages());
        assertTrue(combined.contains("VersionGuard"));
        assertTrue(combined.contains("26.3"));
        assertTrue(combined.contains("Steve"));
        assertTrue(combined.contains("allowed"));

        RecordingAudience denied = new RecordingAudience("Player", false);
        service.execute(denied, new String[]{"reload"});
        assertEquals(1, denied.plainMessages().size());
        assertTrue(denied.plainMessages().getFirst().toLowerCase().contains("permission"));
    }

    private static final class StubLookup implements PlayerLookup {
        private final InspectedPlayer player;

        private StubLookup(InspectedPlayer player) {
            this.player = player;
        }

        @Override
        public Optional<InspectedPlayer> find(String name) {
            return player.name().equalsIgnoreCase(name) ? Optional.of(player) : Optional.empty();
        }

        @Override
        public List<String> onlineNames() {
            return List.of(player.name());
        }
    }

    private static final class RecordingAudience implements CommandAudience {
        private final String name;
        private final boolean admin;
        private final List<Component> messages = new ArrayList<>();

        private RecordingAudience(String name, boolean admin) {
            this.name = name;
            this.admin = admin;
        }

        @Override
        public String name() {
            return name;
        }

        @Override
        public boolean hasPermission(String permission) {
            return admin && permission.endsWith("admin");
        }

        @Override
        public void sendMessage(Component message) {
            messages.add(message);
        }

        private List<String> plainMessages() {
            PlainTextComponentSerializer serializer = PlainTextComponentSerializer.plainText();
            return messages.stream().map(serializer::serialize).toList();
        }
    }

    private static final class RecordingLogger implements com.versionguard.logging.PluginLogger {
        @Override
        public void info(String message) {
        }

        @Override
        public void warn(String message) {
        }

        @Override
        public void error(String message) {
        }

        @Override
        public void error(String message, Throwable throwable) {
        }

        @Override
        public void debug(String message) {
        }
    }
}
