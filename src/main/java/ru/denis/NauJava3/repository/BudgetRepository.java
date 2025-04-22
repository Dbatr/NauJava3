package ru.denis.NauJava3.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;
import ru.denis.NauJava3.entity.Budget;
import ru.denis.NauJava3.entity.User;

import java.util.List;

/**
 * Репозиторий для работы с бюджетами.
 * Предоставляет базовые операции CRUD для сущности {@link Budget}.
 * Позволяет выполнять основные операции по созданию, чтению, обновлению и удалению бюджетов.
 */
@RepositoryRestResource(path = "budgets")
public interface BudgetRepository extends CrudRepository<Budget, Long> {
    List<Budget> findByUser(User currentUser);
}
