package ru.denis.NauJava3.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

/**
 * Сущность, представляющая банковский счет в системе.
 * Хранит информацию о балансе, типе счета и связанных транзакциях.
 */
@Entity
@Table(name = "accounts")
@Getter
@Setter
public class Account {
    /** Уникальный идентификатор счета */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Название счета */
    @Column(nullable = false)
    private String name;

    /** Текущий баланс счета */
    @Column(nullable = false)
    private BigDecimal balance;

    /** Валюта счета */
    @Column(nullable = false)
    private String currency;

    /** Тип банковского счета */
    @Column(name = "account_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private AccountType accountType;

    /** Владелец счета */
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /** Список транзакций по счету */
    @OneToMany(mappedBy = "account")
    private List<Transaction> transactions;

    /**
     * Перечисление типов банковских счетов
     */
    public enum AccountType {
        /** Наличные */
        CASH,
        /** Банковская карта */
        CARD,
        /** Депозит */
        DEPOSIT
    }
}
