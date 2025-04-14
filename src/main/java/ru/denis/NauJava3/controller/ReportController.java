package ru.denis.NauJava3.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.denis.NauJava3.entity.Report;
import ru.denis.NauJava3.entity.enums.ReportStatus;
import ru.denis.NauJava3.service.ReportService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@Tag(name = "Report Controller", description = "API для управления отчетами системы")
public class ReportController {

    private final ReportService reportService;

    /**
     * Создает новый отчет и запускает его асинхронное формирование
     * @return ID созданного отчета
     */
    @Operation(
            summary = "Создать отчет",
            description = "Создает новый отчет и асинхронно запускает его формирование в фоновом режиме"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Отчет успешно создан",
                    content = @Content(mediaType = "application/json")
            )
    })
    @PostMapping
    public ResponseEntity<Map<String, Object>> createReport() {
        Long reportId = reportService.createReport();
        reportService.generateReportAsync(reportId);

        Map<String, Object> response = new HashMap<>();
        response.put("id", reportId);
        response.put("message", "Отчет создан и формируется в фоновом режиме");

        return ResponseEntity.ok(response);
    }

    /**
     * Получает содержимое отчета по ID
     * @param id ID отчета
     * @return содержимое отчета или информацию о его статусе
     */
    @Operation(
            summary = "Получить отчет",
            description = "Получает содержимое отчета по его ID или информацию о статусе формирования"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Отчет успешно получен",
                    content = {
                            @Content(mediaType = "application/json"),
                            @Content(mediaType = "text/html")
                    }
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Отчет не найден",
                    content = @Content
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> getReport(@PathVariable Long id) {
        Report report = reportService.getReport(id);

        if (report.getStatus() == ReportStatus.CREATED) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", report.getStatus());
            response.put("message", "Отчет находится в процессе формирования");
            return ResponseEntity.ok(response);
        } else if (report.getStatus() == ReportStatus.ERROR) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", report.getStatus());
            response.put("message", "При формировании отчета произошла ошибка");
            response.put("error", report.getContent());
            return ResponseEntity.ok(response);
        }

        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .body(report.getContent());
    }
}
