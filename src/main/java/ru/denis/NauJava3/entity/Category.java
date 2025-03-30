package ru.denis.NauJava3.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import ru.denis.NauJava3.entity.enums.OperationType;

import java.util.List;

/**
 * Сущность, представляющая категорию финансовых операций.
 * Используется для классификации транзакций и бюджетов,
 * помогает группировать и анализировать финансовые операции.
 */
@Entity
@Table(name = "categories")
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Category {
    /** Уникальный идентификатор категории */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Название категории */
    @Column(nullable = false)
    private String name;

    /** Описание категории */
    @Column
    private String description;

    /** Тип операций категории (доход/расход) */
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private OperationType type;

    /** Цветовой код для визуального отображения категории */
    @Column(name = "color_code")
    private String colorCode;

    /** Список транзакций, относящихся к данной категории */
    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Transaction> transactions;

    /** Список бюджетов, установленных для данной категории */
    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Budget> budgets;

}
