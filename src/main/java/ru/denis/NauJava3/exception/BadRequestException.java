package ru.denis.NauJava3.exception;

/**
 * Исключение, выбрасываемое при некорректном запросе к API.
 * Используется для обозначения ошибок валидации входных данных.
 */
public class BadRequestException extends RuntimeException {
    /**
     * Создает новое исключение с указанным сообщением об ошибке.
     *
     * @param message сообщение об ошибке
     */
    public BadRequestException(String message) {
        super(message);
    }
}
