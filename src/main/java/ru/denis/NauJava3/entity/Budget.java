package ru.denis.NauJava3.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Сущность, представляющая бюджет в системе.
 * Используется для планирования и контроля расходов по определенной категории
 * в заданный период времени.
 */
@Entity
@Table(name = "budgets")
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Budget {
    /** Уникальный идентификатор бюджета */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Название бюджета */
    @Column(nullable = false)
    private String name;

    /** Лимит расходов/доходов по бюджету */
    @Column(name = "amount_limit", nullable = false)
    private BigDecimal amountLimit;

    /** Дата начала периода действия бюджета */
    @Column(name = "period_start", nullable = false)
    private LocalDate periodStart;

    /** Дата окончания периода действия бюджета */
    @Column(name = "period_end", nullable = false)
    private LocalDate periodEnd;

    /** Пользователь, которому принадлежит бюджет */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnoreProperties("budgets")
    private User user;

    /** Категория, для которой установлен бюджет */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    @JsonIgnoreProperties("budgets")
    private Category category;
}
