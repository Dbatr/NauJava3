package ru.denis.NauJava3.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.denis.NauJava3.repository.UserRepository;

/**
 * Контроллер для отображения веб-страниц, связанных с пользователями.
 * Обрабатывает запросы для просмотра информации о пользователях в веб-интерфейсе.
 *
 * <p>Базовый путь для всех endpoints: {@code /view/users}
 *
 * <p>Использует Thymeleaf для рендеринга HTML страниц.
 */
@Controller
@RequestMapping("/view/users")
public class UserViewController {

    @Autowired
    private UserRepository userRepository;

    /**
     * Отображает список всех пользователей.
     *
     * <p>GET запрос на {@code /view/users} возвращает HTML страницу
     * со списком всех пользователей в системе.
     *
     * @param model модель Spring MVC для передачи данных в представление
     * @return имя представления "users" для отображения списка пользователей
     */
    @GetMapping
    public String listUsers(Model model) {
        model.addAttribute("users", userRepository.findAll());
        return "users";
    }
}
