package ru.denis.NauJava3.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import ru.denis.NauJava3.dto.UserRequest;
import ru.denis.NauJava3.entity.User;
import ru.denis.NauJava3.entity.enums.Role;
import ru.denis.NauJava3.repository.UserRepository;
import ru.denis.NauJava3.service.impl.UserServiceImpl;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Тестовый класс для проверки функциональности {@link UserServiceImpl}.
 */
@SpringBootTest
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private UserRequest userRequest;
    private User user;

    @BeforeEach
    void setUp() {
        userRequest = new UserRequest();
        userRequest.setUsername("testUser");
        userRequest.setEmail("test@example.com");
        userRequest.setPassword("password123");

        user = new User();
        user.setId(1L);
        user.setUsername("testUser");
        user.setEmail("test@example.com");
        user.setPassword("password123");
        user.setRegistrationDate(LocalDateTime.now());
    }

    /**
     * Тест успешного поиска пользователя по имени.
     * Проверяет, что метод возвращает существующего пользователя с правильными данными.
     */
    @Test
    void findByUsername_WhenUserExists_ReturnsUser() {
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));

        User foundUser = userService.findByUsername("testUser");

        assertNotNull(foundUser);
        assertEquals("testUser", foundUser.getUsername());
        assertEquals("test@example.com", foundUser.getEmail());
    }

    /**
     * Тест поиска пользователя, который не существует.
     * Проверяет, что метод выбрасывает исключение {@link EntityNotFoundException}.
     */
    @Test
    void findByUsername_WhenUserDoesNotExist_ThrowsException() {
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
                userService.findByUsername("nonexistent")
        );
    }

    /**
     * Тест успешного добавления нового пользователя.
     * Проверяет, что метод создаёт пользователя с ролью {@link Role#USER} и корректными данными.
     */
    @Test
    void addUser_WhenUserIsValid_ReturnsCreatedUserWithUserRole() {
        when(userRepository.findByUsername(any())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(any())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            savedUser.setId(1L);
            return savedUser;
        });

        User createdUser = userService.addUser(userRequest);

        assertNotNull(createdUser);
        assertEquals(1L, createdUser.getId());
        assertEquals(userRequest.getUsername(), createdUser.getUsername());
        assertEquals(userRequest.getEmail(), createdUser.getEmail());
        assertNotNull(createdUser.getRegistrationDate());
        assertTrue(createdUser.getRoles().contains(Role.USER));
        assertEquals(1, createdUser.getRoles().size());
    }

    /**
     * Тест добавления пользователя с уже существующим именем.
     * Проверяет, что метод выбрасывает исключение {@link IllegalArgumentException}.
     */
    @Test
    void addUser_WhenUsernameExists_ThrowsException() {
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));

        assertThrows(IllegalArgumentException.class, () ->
                userService.addUser(userRequest)
        );
    }

    /**
     * Тест добавления пользователя с уже существующим email.
     * Проверяет, что метод выбрасывает исключение {@link IllegalArgumentException}.
     */
    @Test
    void addUser_WhenEmailExists_ThrowsException() {
        when(userRepository.findByUsername(any())).thenReturn(Optional.empty());
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        assertThrows(IllegalArgumentException.class, () ->
                userService.addUser(userRequest)
        );
    }
}