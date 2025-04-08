package ru.denis.NauJava3.repository;

import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;
import ru.denis.NauJava3.entity.Transaction;
import org.springframework.data.repository.CrudRepository;

/**
 * Репозиторий для работы с транзакциями.
 * Предоставляет базовые операции CRUD для сущности {@link Transaction}.
 */
@RepositoryRestResource(path = "transactions")
public interface TransactionRepository extends CrudRepository<Transaction, Long> {
}