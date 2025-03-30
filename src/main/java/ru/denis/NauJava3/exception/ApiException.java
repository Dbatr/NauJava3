package ru.denis.NauJava3.exception;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Класс для представления информации об ошибках API.
 * Используется для формирования унифицированного ответа при возникновении исключений.
 */
@Getter
@Setter
public class ApiException {
    /** Сообщение об ошибке */
    private String message;

    /** Дополнительные детали ошибки */
    private String details;

    /** Путь запроса, вызвавшего ошибку */
    private String path;

    /** Временная метка возникновения ошибки */
    private LocalDateTime timestamp;

    /** HTTP статус код ошибки */
    private int status;

    /**
     * Приватный конструктор для создания экземпляра через фабричные методы.
     *
     * @param message сообщение об ошибке
     */
    private ApiException(String message) {
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }

    /**
     * Создает экземпляр ApiException из исключения.
     *
     * @param e исключение
     * @return новый экземпляр ApiException
     */
    public static ApiException create(Throwable e) {
        return new ApiException(e.getMessage());
    }

    /**
     * Создает экземпляр ApiException из сообщения об ошибке.
     *
     * @param message сообщение об ошибке
     * @return новый экземпляр ApiException
     */
    public static ApiException create(String message) {
        return new ApiException(message);
    }
}
