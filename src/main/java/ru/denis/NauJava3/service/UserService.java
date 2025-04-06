package ru.denis.NauJava3.service;

import ru.denis.NauJava3.dto.UserRequest;
import ru.denis.NauJava3.entity.User;

/**
 * Интерфейс сервиса для управления пользователями.
 */
public interface UserService {
    User findByUsername(String username);
    User addUser(UserRequest userRequest);
}
