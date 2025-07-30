package ru.yandex.practicum.manager;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {

    @Test
    void getDefaultManager() {
        assertNotNull(new Managers().getDefault());
    }

    @Test
    void getDefaultHistoryManager() {
        assertNotNull(new Managers().getDefaultHistory());
    }
}