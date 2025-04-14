package ru.denis.NauJava3.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import ru.denis.NauJava3.entity.enums.ReportStatus;

/**
 * Сущность отчета для хранения статистики.
 */
@Entity
@Table(name = "reports")
@Getter
@Setter
public class Report {

    /**
     * Уникальный идентификатор отчета
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Статус отчета (создан, завершен, ошибка)
     * По умолчанию имеет значение "создан"
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReportStatus status = ReportStatus.CREATED;

    /**
     * Содержимое отчета в формате HTML
     * Хранится в виде текстового поля для обеспечения достаточного размера
     */
    @Column(columnDefinition = "TEXT")
    private String content;

    /**
     * Время, затраченное на формирование отчета (в миллисекундах)
     */
    @Column(name = "creation_time")
    private Long creationTime;
}
