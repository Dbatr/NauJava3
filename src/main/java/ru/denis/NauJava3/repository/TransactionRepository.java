package ru.denis.NauJava3.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.denis.NauJava3.entity.Transaction;

import java.util.ArrayList;
import java.util.List;

/**
 * Репозиторий для работы с транзакциями
 */
@Repository
public class TransactionRepository implements CrudRepository<Transaction, Long> {
    private final List<Transaction> transactionContainer;
    private Long nextId = 1L; // Счетчик для генерации ID

    @Autowired
    public TransactionRepository(List<Transaction> transactionContainer) {
        this.transactionContainer = transactionContainer;
    }

    @Override
    public void create(Transaction entity) {
        entity.setId(nextId++);
        transactionContainer.add(entity);
    }

    @Override
    public Transaction read(Long id) {
        return transactionContainer.stream()
                .filter(transaction -> transaction.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    @Override
    public void update(Transaction entity) {
        for (int i = 0; i < transactionContainer.size(); i++) {
            if (transactionContainer.get(i).getId().equals(entity.getId())) {
                transactionContainer.set(i, entity);
                return;
            }
        }
    }

    @Override
    public void delete(Long id) {
        transactionContainer.removeIf(transaction -> transaction.getId().equals(id));
    }

    @Override
    public List<Transaction> findAll() {
        return new ArrayList<>(transactionContainer);
    }
}
