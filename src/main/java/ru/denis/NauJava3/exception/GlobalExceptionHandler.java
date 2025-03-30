package ru.denis.NauJava3.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Глобальный обработчик исключений для всего приложения.
 * Преобразует различные исключения в унифицированный формат ответа API.
 *
 * @see ApiException
 * @see ControllerAdvice
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Обрабатывает исключения типа ResourceNotFoundException.
     * Возвращает ответ с HTTP статусом 404 (Not Found).
     *
     * @param ex исключение
     * @param request HTTP запрос
     * @return ResponseEntity с информацией об ошибке
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ApiException> handleResourceNotFoundException(
            ResourceNotFoundException ex,
            HttpServletRequest request) {
        ApiException apiException = ApiException.create(ex);
        apiException.setStatus(HttpStatus.NOT_FOUND.value());
        apiException.setPath(request.getRequestURI());
        apiException.setDetails("Запрашиваемый ресурс не найден");
        return new ResponseEntity<>(apiException, HttpStatus.NOT_FOUND);
    }

    /**
     * Обрабатывает исключения типа BadRequestException.
     * Возвращает ответ с HTTP статусом 400 (Bad Request).
     *
     * @param ex исключение
     * @param request HTTP запрос
     * @return ResponseEntity с информацией об ошибке
     */
    @ExceptionHandler(BadRequestException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiException> handleBadRequestException(
            BadRequestException ex,
            HttpServletRequest request) {
        ApiException apiException = ApiException.create(ex);
        apiException.setStatus(HttpStatus.BAD_REQUEST.value());
        apiException.setPath(request.getRequestURI());
        apiException.setDetails("Некорректный запрос");
        return new ResponseEntity<>(apiException, HttpStatus.BAD_REQUEST);
    }

    /**
     * Обрабатывает все остальные исключения.
     * Возвращает ответ с HTTP статусом 500 (Internal Server Error).
     *
     * @param ex исключение
     * @param request HTTP запрос
     * @return ResponseEntity с информацией об ошибке
     */
    @ExceptionHandler(Exception.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ApiException> handleAllExceptions(
            Exception ex,
            HttpServletRequest request) {
        ApiException apiException = ApiException.create(ex);
        apiException.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        apiException.setPath(request.getRequestURI());
        apiException.setDetails("Внутренняя ошибка сервера");
        return new ResponseEntity<>(apiException, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
