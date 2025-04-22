package ru.denis.NauJava3.selenium;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.denis.NauJava3.entity.User;
import ru.denis.NauJava3.entity.enums.Role;
import ru.denis.NauJava3.repository.UserRepository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Тест для проверки функциональности входа и выхода из системы с использованием Selenium WebDriver.
 * Запускает реальный браузер и взаимодействует с приложением как пользователь.
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LoginLogoutSeleniumTest {

    @LocalServerPort
    private int port;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private WebDriver driver;
    private WebDriverWait wait;
    private String baseUrl;

    private final String USERNAME = "testuser";
    private final String PASSWORD = "password123";

    /**
     * Подготовка тестового окружения перед каждым тестом.
     * Создаёт тестового пользователя и настраивает Selenium WebDriver.
     */
    @BeforeEach
    public void setup() {
        createTestUser();

        baseUrl = "http://localhost:" + port;
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");

        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    /**
     * Создаёт тестового пользователя для входа в систему, если он ещё не существует.
     */
    private void createTestUser() {
        Optional<User> existingUser = userRepository.findByUsername(USERNAME);

        if (existingUser.isEmpty()) {
            User user = new User();
            user.setUsername(USERNAME);
            String EMAIL = "testuser@example.com";
            user.setEmail(EMAIL);
            user.setPassword(passwordEncoder.encode(PASSWORD));
            user.setRegistrationDate(LocalDateTime.now());

            Set<Role> roles = new HashSet<>();
            roles.add(Role.USER);
            user.setRoles(roles);

            userRepository.save(user);
        }
    }

    /**
     * Очистка тестового окружения после каждого теста.
     * Закрывает браузер и удаляет тестового пользователя.
     */
    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
        userRepository.findByUsername(USERNAME).ifPresent(userRepository::delete);
    }

    /**
     * Тестирует сценарий входа и выхода пользователя из системы.
     * <p>
     * Тест выполняет следующие шаги:
     * <ol>
     *   <li>Переход на страницу входа по URL-адресу</li>
     *   <li>Проверка отображения формы входа с заголовком "Вход в систему"</li>
     *   <li>Ввод учетных данных тестового пользователя (логин и пароль)</li>
     *   <li>Нажатие на кнопку входа</li>
     *   <li>Проверка успешного входа: отображается заголовок "Добро пожаловать"</li>
     *   <li>Проверка отображения информации о пользователе</li>
     *   <li>Проверка корректного отображения имени вошедшего пользователя</li>
     *   <li>Нажатие на кнопку выхода из системы</li>
     *   <li>Проверка успешного выхода: отображается страница входа или сообщение о выходе</li>
     *   <li>Проверка возврата на страницу входа с заголовком "Вход в систему"</li>
     * </ol>
     * </p>
     *
     * @throws org.openqa.selenium.TimeoutException если ожидаемые элементы не появляются за отведенное время
     * @throws org.openqa.selenium.NoSuchElementException если элементы не найдены на странице
     */
    @Test
    public void testLoginAndLogout() {
        driver.get(baseUrl + "/login");

        WebElement loginTitle = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//h2[text()='Вход в систему']")));
        assertTrue(loginTitle.isDisplayed(), "Заголовок страницы входа не отображается");

        WebElement usernameInput = driver.findElement(By.id("username"));
        WebElement passwordInput = driver.findElement(By.id("password"));

        usernameInput.sendKeys(USERNAME);
        passwordInput.sendKeys(PASSWORD);

        WebElement loginButton = driver.findElement(By.xpath("//button[text()='Войти']"));
        loginButton.click();

        WebElement welcomeHeading = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//h1[text()='Добро пожаловать']")));
        assertTrue(welcomeHeading.isDisplayed(), "Заголовок домашней страницы не отображается");

        WebElement userInfo = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.className("user-info")));
        assertTrue(userInfo.isDisplayed(), "Информация о пользователе не отображается");

        WebElement username = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//p[contains(text(), 'Вы вошли как:')]")));
        assertTrue(username.getText().contains(USERNAME),
                "Имя пользователя не отображается или некорректно");

        WebElement logoutButton = driver.findElement(By.className("logout-btn"));
        logoutButton.click();

        wait.until(ExpectedConditions.or(
                ExpectedConditions.visibilityOfElementLocated(By.id("username")),
                ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//div[contains(@class, 'success') and contains(text(), 'Вы успешно вышли из системы')]"))
        ));

        WebElement loginTitleAfterLogout = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//h2[text()='Вход в систему']")));
        assertTrue(loginTitleAfterLogout.isDisplayed(), "Заголовок страницы входа не отображается после выхода");
    }
}