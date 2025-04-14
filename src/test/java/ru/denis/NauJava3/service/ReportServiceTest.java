package ru.denis.NauJava3.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import ru.denis.NauJava3.entity.Report;
import ru.denis.NauJava3.entity.Transaction;
import ru.denis.NauJava3.entity.enums.ReportStatus;
import ru.denis.NauJava3.repository.ReportRepository;
import ru.denis.NauJava3.repository.TransactionRepository;
import ru.denis.NauJava3.repository.UserRepository;
import ru.denis.NauJava3.service.impl.ReportServiceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Модульные тесты для сервиса отчетов {@link ReportServiceImpl}.
 * Тесты охватывают различные сценарии генерации отчетов, включая успешные
 * и ошибочные случаи при асинхронной обработке.
 *
 * @see ReportService
 * @see Report
 */
@ExtendWith(MockitoExtension.class)
public class ReportServiceTest {
    @Mock
    private ReportRepository reportRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private TemplateEngine templateEngine;

    @InjectMocks
    private ReportServiceImpl reportService;

    private Report testReport;

    /**
     * Инициализация тестовых данных перед каждым тестом.
     */
    @BeforeEach
    void setUp() {
        testReport = new Report();
        testReport.setId(1L);
        testReport.setStatus(ReportStatus.CREATED);
    }

    /**
     * Проверяет получение отчета по существующему идентификатору.
     */
    @Test
    void getReport_ExistingId_ShouldReturnReport() {
        when(reportRepository.findById(1L)).thenReturn(Optional.of(testReport));

        Report result = reportService.getReport(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(ReportStatus.CREATED, result.getStatus());
        verify(reportRepository, times(1)).findById(1L);
    }

    /**
     * Проверяет, что при запросе несуществующего отчета выбрасывается исключение.
     */
    @Test
    void getReport_NonExistingId_ShouldThrowEntityNotFoundException() {
        when(reportRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> reportService.getReport(999L));
        verify(reportRepository, times(1)).findById(999L);
    }

    /**
     * Проверяет создание нового отчета и возврат его идентификатора.
     */
    @Test
    void createReport_ShouldReturnNewReportId() {
        Report savedReport = new Report();
        savedReport.setId(1L);
        savedReport.setStatus(ReportStatus.CREATED);

        when(reportRepository.save(any(Report.class))).thenReturn(savedReport);

        Long reportId = reportService.createReport();

        assertEquals(1L, reportId);
        verify(reportRepository, times(1)).save(any(Report.class));
    }

    /**
     * Проверяет успешный сценарий асинхронной генерации отчета.
     * Отчет должен быть обновлен и получить статус COMPLETED.
     */
    @Test
    void generateReportAsync_SuccessfulReport_ShouldUpdateReportStatus() throws InterruptedException, ExecutionException, TimeoutException {
        when(reportRepository.findById(1L)).thenReturn(Optional.of(testReport));
        when(userRepository.count()).thenReturn(5L);

        List<Transaction> transactions = new ArrayList<>();
        transactions.add(new Transaction());
        transactions.add(new Transaction());
        when(transactionRepository.findAll()).thenReturn(transactions);

        when(templateEngine.process(eq("report-template"), any(Context.class))).thenReturn("<html>Test Report</html>");

        ArgumentCaptor<Report> reportCaptor = ArgumentCaptor.forClass(Report.class);
        when(reportRepository.save(reportCaptor.capture())).thenReturn(testReport);

        CompletableFuture<Void> future = reportService.generateReportAsync(1L);
        future.get(5, TimeUnit.SECONDS);

        verify(reportRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).count();
        verify(transactionRepository, times(1)).findAll();
        verify(templateEngine, times(1)).process(eq("report-template"), any(Context.class));
        verify(reportRepository, times(1)).save(any(Report.class));

        Report savedReport = reportCaptor.getValue();
        assertEquals(ReportStatus.COMPLETED, savedReport.getStatus());
        assertEquals("<html>Test Report</html>", savedReport.getContent());
    }

    /**
     * Проверяет обработку ошибки при получении количества пользователей.
     * Отчет должен быть обновлен со статусом ERROR и содержать информацию об ошибке.
     */
    @Test
    void generateReportAsync_ExceptionInUserCount_ShouldUpdateReportWithErrorStatus() throws InterruptedException {
        when(reportRepository.findById(1L)).thenReturn(Optional.of(testReport));
        doThrow(new RuntimeException("Database error")).when(userRepository).count();

        ArgumentCaptor<Report> reportCaptor = ArgumentCaptor.forClass(Report.class);
        doAnswer(invocation -> {
            Report report = invocation.getArgument(0);
            reportCaptor.capture();
            return report;
        }).when(reportRepository).save(any(Report.class));

        reportService.generateReportAsync(1L);

        Thread.sleep(500);

        verify(reportRepository, atLeastOnce()).findById(1L);
        verify(userRepository, times(1)).count();
        verify(reportRepository, atLeastOnce()).save(any(Report.class));

        if (!reportCaptor.getAllValues().isEmpty()) {
            Report savedReport = reportCaptor.getValue();
            assertEquals(ReportStatus.ERROR, savedReport.getStatus());
            assertTrue(savedReport.getContent().contains("Database error"));
        }
    }

    /**
     * Проверяет обработку ошибки при получении списка транзакций.
     * Отчет должен быть обновлен со статусом ERROR и содержать информацию об ошибке.
     */
    @Test
    void generateReportAsync_ExceptionInTransactionList_ShouldUpdateReportWithErrorStatus() throws InterruptedException {
        when(reportRepository.findById(1L)).thenReturn(Optional.of(testReport));
        when(userRepository.count()).thenReturn(5L);
        doThrow(new RuntimeException("Transaction error")).when(transactionRepository).findAll();

        ArgumentCaptor<Report> reportCaptor = ArgumentCaptor.forClass(Report.class);
        doAnswer(invocation -> {
            Report report = invocation.getArgument(0);
            reportCaptor.capture();
            return report;
        }).when(reportRepository).save(any(Report.class));

        reportService.generateReportAsync(1L);

        Thread.sleep(500);

        verify(reportRepository, atLeastOnce()).findById(1L);
        verify(userRepository, times(1)).count();
        verify(transactionRepository, times(1)).findAll();
        verify(reportRepository, atLeastOnce()).save(any(Report.class));

        if (!reportCaptor.getAllValues().isEmpty()) {
            Report savedReport = reportCaptor.getValue();
            assertEquals(ReportStatus.ERROR, savedReport.getStatus());
            assertTrue(savedReport.getContent().contains("Transaction error"));
        }
    }

    /**
     * Проверяет обработку ошибки при обработке шаблона.
     * Отчет должен быть обновлен со статусом ERROR и содержать информацию об ошибке.
     */
    @Test
    void generateReportAsync_ExceptionInTemplateProcessing_ShouldUpdateReportWithErrorStatus() throws InterruptedException {
        when(reportRepository.findById(1L)).thenReturn(Optional.of(testReport));
        when(userRepository.count()).thenReturn(5L);

        List<Transaction> transactions = new ArrayList<>();
        transactions.add(new Transaction());
        when(transactionRepository.findAll()).thenReturn(transactions);

        doThrow(new RuntimeException("Template processing error")).when(templateEngine).process(eq("report-template"), any(Context.class));

        ArgumentCaptor<Report> reportCaptor = ArgumentCaptor.forClass(Report.class);
        doAnswer(invocation -> {
            Report report = invocation.getArgument(0);
            reportCaptor.capture();
            return report;
        }).when(reportRepository).save(any(Report.class));

        reportService.generateReportAsync(1L);

        Thread.sleep(500);

        verify(reportRepository, atLeastOnce()).findById(1L);
        verify(userRepository, times(1)).count();
        verify(transactionRepository, times(1)).findAll();
        verify(templateEngine, times(1)).process(eq("report-template"), any(Context.class));
        verify(reportRepository, atLeastOnce()).save(any(Report.class));

        if (!reportCaptor.getAllValues().isEmpty()) {
            Report savedReport = reportCaptor.getValue();
            assertEquals(ReportStatus.ERROR, savedReport.getStatus());
            assertTrue(savedReport.getContent().contains("Template processing error"));
        }
    }

    /**
     * Проверяет корректную обработку ситуации, когда отчет не найден.
     * В этом случае никакие дополнительные методы не должны вызываться.
     */
    @Test
    void generateReportAsync_ReportNotFound_ShouldHandleException() throws InterruptedException {
        when(reportRepository.findById(999L)).thenReturn(Optional.empty());

        reportService.generateReportAsync(999L);

        Thread.sleep(500);

        verify(reportRepository, atLeastOnce()).findById(999L);
        verify(userRepository, never()).count();
        verify(transactionRepository, never()).findAll();
        verify(templateEngine, never()).process(anyString(), any(Context.class));
        verify(reportRepository, never()).save(any(Report.class));
    }
}