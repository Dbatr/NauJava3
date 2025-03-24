package ru.denis.NauJava3.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Сущность, представляющая пользователя в системе.
 * Содержит основную информацию о пользователе и связи с его финансовыми данными.
 */
@Entity
@Table(name = "users")
@Getter
@Setter
public class User {
    /** Уникальный идентификатор пользователя */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Имя пользователя */
    @Column(nullable = false)
    private String username;

    /** Email пользователя (уникальный) */
    @Column(nullable = false, unique = true)
    private String email;

    /** Пароль пользователя */
    @Column(name = "password", nullable = false)
    private String password;

    /** Дата регистрации пользователя */
    @Column(name = "registration_date", nullable = false)
    private LocalDateTime registrationDate;

    /** Список банковских счетов пользователя */
    @OneToMany(mappedBy = "user")
    private List<Account> accounts;

    /** Список транзакций пользователя */
    @OneToMany(mappedBy = "user")
    private List<Transaction> transactions;

    /** Список бюджетов пользователя */
    @OneToMany(mappedBy = "user")
    private List<Budget> budgets;
}
