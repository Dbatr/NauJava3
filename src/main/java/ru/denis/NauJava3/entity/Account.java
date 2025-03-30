package ru.denis.NauJava3.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
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
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
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
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnoreProperties("accounts")
    private User user;

    /** Список транзакций по счету */
    @OneToMany(mappedBy = "account", fetch = FetchType.LAZY)
    @JsonIgnore
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
