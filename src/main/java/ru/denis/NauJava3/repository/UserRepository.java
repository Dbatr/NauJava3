package ru.denis.NauJava3.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.denis.NauJava3.entity.User;

/**
 * Репозиторий для работы с пользователями.
 * Предоставляет базовые операции CRUD для сущности User.
 */
@Repository
public interface UserRepository extends CrudRepository<User, Long> {
}
