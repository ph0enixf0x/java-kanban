package ru.yandex.practicum.manager;

public class ManagerSaveException extends RuntimeException {
  public ManagerSaveException(String message, Throwable cause) {
    super(message, cause);
  }
}
