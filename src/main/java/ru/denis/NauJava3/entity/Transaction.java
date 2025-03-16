package ru.denis.NauJava3.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Сущность финансовой транзакции
 */
public class Transaction {
    private Long id;
    private BigDecimal amount;
    private String category;
    private String description;
    private LocalDateTime date;
    private TransactionType type;

    /**
     * Типы транзакций
     */
    public enum TransactionType {
        INCOME,
        EXPENSE
    }

    public Long getId() {
        return id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public TransactionType getType() {
        return type;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }
}
