package ru.denis.NauJava3.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;
import ru.denis.NauJava3.entity.User;

/**
 * Репозиторий для работы с пользователями.
 * Предоставляет базовые операции CRUD для сущности User.
 */
@RepositoryRestResource(path = "users")
public interface UserRepository extends CrudRepository<User, Long> {
}
