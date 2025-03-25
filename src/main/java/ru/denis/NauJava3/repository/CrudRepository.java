package ru.denis.NauJava3.repository;

import java.util.List;

/**
 * Базовый интерфейс для работы с хранилищем данных
 * @param <T> тип сущности
 * @param <ID> тип идентификатора
 */
public interface CrudRepository<T, ID> {
    /** Создает новую запись */
    void create(T entity);
    /** Читает запись по ID */
    T read(ID id);
    /** Обновляет существующую запись */
    void update(T entity);
    /** Удаляет запись по ID */
    void delete(ID id);
    /** @return список всех записей */
    List<T> findAll();
}
