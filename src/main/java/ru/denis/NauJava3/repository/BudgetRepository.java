package ru.denis.NauJava3.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.denis.NauJava3.entity.Budget;

/**
 * Репозиторий для работы с бюджетами.
 * Предоставляет базовые операции CRUD для сущности {@link Budget}.
 * Позволяет выполнять основные операции по созданию, чтению, обновлению и удалению бюджетов.
 */
@Repository
public interface BudgetRepository extends CrudRepository<Budget, Long> {
}
