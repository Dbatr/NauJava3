package ru.denis.NauJava3.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import ru.denis.NauJava3.entity.enums.OperationType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Сущность, представляющая финансовую транзакцию в системе.
 * Хранит информацию о движении денежных средств: сумму, дату,
 * тип операции, категорию и связанные сущности.
 */
@Entity
@Table(name = "transactions")
@Getter
@Setter
public class Transaction {
    /** Уникальный идентификатор транзакции */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Сумма транзакции */
    @Column(nullable = false)
    private BigDecimal amount;

    /** Дата и время совершения транзакции */
    @Column(nullable = false)
    private LocalDateTime date;

    /** Описание или комментарий к транзакции */
    @Column
    private String description;

    /** Тип операции (доход/расход) */
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private OperationType type;

    /** Пользователь, совершивший транзакцию */
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /** Счет, по которому проведена транзакция */
    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    /** Категория транзакции */
    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

}
