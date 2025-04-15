package ru.denis.NauJava3.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import ru.denis.NauJava3.entity.Report;
import ru.denis.NauJava3.entity.enums.ReportStatus;
import ru.denis.NauJava3.service.ReportService;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Модульные тесты для контроллера отчетов {@link ReportController}.
 * Тесты проверяют корректность обработки HTTP-запросов и формирования ответов
 * в различных сценариях работы с отчетами.
 *
 * @see ReportController
 * @see ReportService
 * @see Report
 */
@ExtendWith(MockitoExtension.class)
class ReportControllerTest {

    @Mock
    private ReportService reportService;

    @InjectMocks
    private ReportController reportController;

    private Report completedReport;
    private Report inProgressReport;
    private Report errorReport;

    /**
     * Настройка тестовых данных перед каждым тестом.
     * Создает тестовые экземпляры отчетов с различными статусами.
     */
    @BeforeEach
    void setUp() {
        completedReport = new Report();
        completedReport.setId(1L);
        completedReport.setStatus(ReportStatus.COMPLETED);
        completedReport.setContent("<html><body>Отчет готов</body></html>");

        inProgressReport = new Report();
        inProgressReport.setId(2L);
        inProgressReport.setStatus(ReportStatus.CREATED);

        errorReport = new Report();
        errorReport.setId(3L);
        errorReport.setStatus(ReportStatus.ERROR);
        errorReport.setContent("Database connection failed");
    }

    /**
     * Проверяет, что метод создания отчета возвращает корректный ID и сообщение.
     * Также проверяет, что методы сервиса вызываются с правильными параметрами.
     */
    @Test
    void createReport_ShouldReturnReportId() {
        // Arrange
        Long reportId = 4L;
        when(reportService.createReport()).thenReturn(reportId);

        // Act
        ResponseEntity<Map<String, Object>> response = reportController.createReport();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());

        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(reportId, responseBody.get("id"));
        assertEquals("Отчет создан и формируется в фоновом режиме", responseBody.get("message"));

        verify(reportService).createReport();
        verify(reportService).generateReportAsync(reportId);
    }

    /**
     * Проверяет корректность обработки запроса для готового отчета.
     * Готовый отчет должен возвращаться в виде HTML-содержимого.
     */
    @Test
    void getReport_CompletedReport_ShouldReturnHtmlContent() {
        // Arrange
        when(reportService.getReport(1L)).thenReturn(completedReport);

        // Act
        ResponseEntity<?> response = reportController.getReport(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MediaType.TEXT_HTML, response.getHeaders().getContentType());
        assertEquals("<html><body>Отчет готов</body></html>", response.getBody());

        verify(reportService).getReport(1L);
    }

    /**
     * Проверяет корректность обработки запроса для отчета, который находится в процессе формирования.
     * Должно возвращаться информационное сообщение о статусе отчета.
     */
    @Test
    void getReport_InProgressReport_ShouldReturnStatusMessage() {
        // Arrange
        when(reportService.getReport(2L)).thenReturn(inProgressReport);

        // Act
        ResponseEntity<?> response = reportController.getReport(2L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());

        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();

        assertNotNull(responseBody);
        assertEquals(ReportStatus.CREATED.toString(), responseBody.get("status").toString());
        assertEquals("Отчет находится в процессе формирования", responseBody.get("message"));

        verify(reportService).getReport(2L);
    }

    /**
     * Проверяет корректность обработки запроса для отчета, который завершился с ошибкой.
     * Должно возвращаться сообщение об ошибке и информация о причине ошибки.
     */
    @Test
    void getReport_ErrorReport_ShouldReturnErrorMessage() {
        // Arrange
        when(reportService.getReport(3L)).thenReturn(errorReport);

        // Act
        ResponseEntity<?> response = reportController.getReport(3L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());

        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();

        assertNotNull(responseBody);
        assertEquals(ReportStatus.ERROR.toString(), responseBody.get("status").toString());
        assertEquals("При формировании отчета произошла ошибка", responseBody.get("message"));
        assertEquals("Database connection failed", responseBody.get("error"));

        verify(reportService).getReport(3L);
    }

    /**
     * Проверяет корректность обработки исключения при запросе несуществующего отчета.
     * Метод должен пробрасывать исключение для дальнейшей обработки глобальным обработчиком исключений.
     */
    @Test
    void getReport_NonExistentReport_ShouldThrowException() {
        // Arrange
        when(reportService.getReport(999L)).thenThrow(new RuntimeException("Отчет не найден"));

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            reportController.getReport(999L);
        });

        assertEquals("Отчет не найден", exception.getMessage());
        verify(reportService).getReport(999L);
    }
}