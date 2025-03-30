package ru.denis.NauJava3.exception;

/**
 * Исключение, выбрасываемое когда запрашиваемый ресурс не найден.
 * Предоставляет два конструктора для различных сценариев использования.
 */
public class ResourceNotFoundException extends RuntimeException {
    /**
     * Создает новое исключение с указанным сообщением об ошибке.
     *
     * @param message сообщение об ошибке
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }

    /**
     * Создает новое исключение с форматированным сообщением об ошибке.
     * Формат сообщения: "{resourceName} не найден с {fieldName} : '{fieldValue}'"
     *
     * @param resourceName название ресурса
     * @param fieldName название поля, по которому искался ресурс
     * @param fieldValue значение поля
     */
    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s не найден с %s : '%s'", resourceName, fieldName, fieldValue));
    }
}
