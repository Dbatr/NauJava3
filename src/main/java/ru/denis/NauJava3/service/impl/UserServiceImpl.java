package ru.denis.NauJava3.service.impl;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.denis.NauJava3.dto.UserRequest;
import ru.denis.NauJava3.entity.User;
import ru.denis.NauJava3.entity.enums.Role;
import ru.denis.NauJava3.repository.UserRepository;
import ru.denis.NauJava3.service.UserService;

import java.time.LocalDateTime;

/**
 * Реализация сервиса для управления пользователями в системе.
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    /**
     * Находит пользователя по его имени.
     *
     * @param username имя пользователя для поиска
     * @return объект {@link User}, соответствующий указанному имени
     * @throws EntityNotFoundException если пользователь с указанным именем не найден
     */
    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден: " + username));
    }

    /**
     * Добавляет нового пользователя в систему на основе данных из запроса.
     * Автоматически присваивает пользователю роль {@link Role#USER}.
     *
     * @param userRequest объект запроса с данными нового пользователя
     * @return созданный объект {@link User} с установленным идентификатором и датой регистрации
     * @throws IllegalArgumentException если имя пользователя или email уже существуют в системе
     */
    @Override
    @Transactional
    public User addUser(UserRequest userRequest) {
        if (userRepository.findByUsername(userRequest.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Пользователь с таким именем уже существует");
        }

        if (userRepository.findByEmail(userRequest.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Пользователь с таким email уже существует");
        }

        User user = new User();
        user.setUsername(userRequest.getUsername());
        user.setEmail(userRequest.getEmail());
        user.setPassword(userRequest.getPassword());
        user.setRegistrationDate(LocalDateTime.now());

        user.getRoles().add(Role.USER);

        return userRepository.save(user);
    }
}
