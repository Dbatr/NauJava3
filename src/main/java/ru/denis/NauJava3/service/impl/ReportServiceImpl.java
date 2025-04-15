package ru.denis.NauJava3.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import ru.denis.NauJava3.entity.Report;
import ru.denis.NauJava3.entity.Transaction;
import ru.denis.NauJava3.entity.enums.ReportStatus;
import ru.denis.NauJava3.repository.ReportRepository;
import ru.denis.NauJava3.repository.TransactionRepository;
import ru.denis.NauJava3.repository.UserRepository;
import ru.denis.NauJava3.service.ReportService;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Реализация сервиса для работы с отчетами.
 * Предоставляет методы для создания, получения и асинхронного формирования отчетов
 * с использованием многопоточной обработки.
 */
@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final TemplateEngine templateEngine;

    /**
     * Получает отчет по его идентификатору.
     *
     * @param id идентификатор отчета
     * @return объект отчета
     * @throws EntityNotFoundException если отчет с указанным идентификатором не найден
     */
    @Override
    public Report getReport(Long id) {
        return reportRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Отчет с ID " + id + " не найден"));
    }

    /**
     * Создает новый отчет со статусом "создан".
     *
     * @return идентификатор созданного отчета
     */
    @Override
    public Long createReport() {
        Report report = new Report();
        report.setStatus(ReportStatus.CREATED);
        Report savedReport = reportRepository.save(report);
        return savedReport.getId();
    }

    /**
     * Асинхронно формирует отчет в отдельных потоках.
     * Метод вычисляет количество пользователей и получает список транзакций в отдельных потоках,
     * а затем объединяет результаты в HTML-отчет с использованием Thymeleaf шаблона.
     *
     * @param reportId идентификатор отчета для формирования
     * @return объект CompletableFuture для асинхронного выполнения
     */
    @Override
    public CompletableFuture<Void> generateReportAsync(Long reportId) {
        return CompletableFuture.runAsync(() -> {
            try {
                Report report = getReport(reportId);

                long startTotalTime = System.currentTimeMillis();
                AtomicLong userCountTime = new AtomicLong();
                AtomicLong transactionsTime = new AtomicLong();
                AtomicLong userCount = new AtomicLong();

                // Поток подсчета пользователей
                Thread userCountThread = new Thread(() -> {
                    long startTime = System.currentTimeMillis();
                    userCount.set(userRepository.count());
                    userCountTime.set(System.currentTimeMillis() - startTime);
                });

                // Поток списка транзакций
                AtomicLong transactionCount = new AtomicLong();
                Thread transactionsThread = new Thread(() -> {
                    long startTime = System.currentTimeMillis();
                    List<Transaction> transactions = (List<Transaction>) transactionRepository.findAll();
                    transactionCount.set(transactions.size());
                    transactionsTime.set(System.currentTimeMillis() - startTime);
                });

                userCountThread.start();
                transactionsThread.start();
                userCountThread.join();
                transactionsThread.join();

                long totalTime = System.currentTimeMillis() - startTotalTime;

                Context context = new Context();
                context.setVariable("userCount", userCount.get());
                context.setVariable("userCountTime", userCountTime.get());
                context.setVariable("transactionCount", transactionCount.get());
                context.setVariable("transactionsTime", transactionsTime.get());
                context.setVariable("totalTime", totalTime);
                String htmlContent = templateEngine.process("report-template", context);

                report.setContent(htmlContent);
                report.setStatus(ReportStatus.COMPLETED);
                report.setCreationTime(totalTime);
                reportRepository.save(report);

            } catch (Exception e) {
                Report report = getReport(reportId);
                report.setStatus(ReportStatus.ERROR);
                report.setContent("Ошибка при формировании отчета: " + e.getMessage());
                reportRepository.save(report);
            }
        });
    }
}
