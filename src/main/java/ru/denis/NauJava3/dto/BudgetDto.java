package ru.denis.NauJava3.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * DTO для создания нового бюджета
 */
@Data
public class BudgetDto {
    /**
     * Название бюджета
     */
    @NotBlank(message = "Название бюджета не может быть пустым")
    private String name;

    /**
     * Лимит расходов/доходов по бюджету
     */
    @NotNull(message = "Лимит бюджета должен быть указан")
    @DecimalMin(value = "0.01", message = "Лимит бюджета должен быть положительным числом")
    private BigDecimal amountLimit;

    /**
     * Дата начала периода действия бюджета
     */
    @NotNull(message = "Дата начала периода должна быть указана")
    private LocalDate periodStart;

    /**
     * Дата окончания периода действия бюджета
     */
    @NotNull(message = "Дата окончания периода должна быть указана")
    private LocalDate periodEnd;

    /**
     * ID категории, для которой установлен бюджет
     */
    @NotNull(message = "ID категории должен быть указан")
    private Long categoryId;
}
