package ru.yandex.practicum.manager;

import ru.yandex.practicum.tasks.Task;
import java.util.ArrayList;

public interface HistoryManager {

    void add(Task task);

    ArrayList<Task> getHistory();
}
