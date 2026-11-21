package com.versionguard.version;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MinecraftVersionTest {

    @Test
    void parsesHistoricAndCalendarVersions() {
        assertEquals(MinecraftVersion.of(1, 21, 0), MinecraftVersion.parse("1.21"));
        assertEquals(MinecraftVersion.of(1, 21, 11), MinecraftVersion.parse("1.21.11"));
        assertEquals(MinecraftVersion.of(26, 3, 0), MinecraftVersion.parse("26.3"));
        assertEquals(MinecraftVersion.of(26, 1, 2), MinecraftVersion.parse("26.1.2"));
    }

    @Test
    void ordersVersionsAcrossNumberingSchemes() {
        assertTrue(MinecraftVersion.parse("1.20.6").compareTo(MinecraftVersion.parse("1.21")) < 0);
        assertTrue(MinecraftVersion.parse("1.21").compareTo(MinecraftVersion.parse("1.21.1")) < 0);
        assertTrue(MinecraftVersion.parse("1.21.11").compareTo(MinecraftVersion.parse("26.1")) < 0);
        assertTrue(MinecraftVersion.parse("26.1.2").compareTo(MinecraftVersion.parse("26.2")) < 0);
        assertTrue(MinecraftVersion.parse("26.2").compareTo(MinecraftVersion.parse("26.3")) < 0);
    }

    @Test
    void rejectsInvalidInput() {
        assertTrue(MinecraftVersion.tryParse("").isEmpty());
        assertTrue(MinecraftVersion.tryParse("snapshot").isEmpty());
        assertTrue(MinecraftVersion.tryParse("1").isEmpty());
        assertTrue(MinecraftVersion.tryParse("26.3-rc1").isEmpty());
    }
}
