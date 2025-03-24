package ru.denis.NauJava3.repository;

import org.springframework.stereotype.Repository;
import ru.denis.NauJava3.entity.Transaction;
import org.springframework.data.repository.CrudRepository;

/**
 * Репозиторий для работы с транзакциями.
 * Предоставляет базовые операции CRUD для сущности {@link Transaction}.
 */
@Repository
public interface TransactionRepository extends CrudRepository<Transaction, Long> {
}