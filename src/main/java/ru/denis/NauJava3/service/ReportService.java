package ru.denis.NauJava3.service;

import ru.denis.NauJava3.entity.Report;
import java.util.concurrent.CompletableFuture;

public interface ReportService {

    /**
     * Получить отчет по ID
     * @param id ID отчета
     * @return отчет
     */
    Report getReport(Long id);

    /**
     * Создать новый отчет
     * @return ID созданного отчета
     */
    Long createReport();

    /**
     * Асинхронно сформировать отчет
     * @param reportId ID отчета для формирования
     * @return CompletableFuture<Void>
     */
    CompletableFuture<Void> generateReportAsync(Long reportId);
}
