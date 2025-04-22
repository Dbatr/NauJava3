package ru.denis.NauJava3.controller;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.denis.NauJava3.dto.BudgetDto;
import ru.denis.NauJava3.entity.Budget;
import ru.denis.NauJava3.entity.Category;
import ru.denis.NauJava3.entity.User;
import ru.denis.NauJava3.exception.ResourceNotFoundException;
import ru.denis.NauJava3.service.BudgetService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

/**
 * Интеграционные тесты для {@link BudgetController} с использованием RestAssured с MockMvc.
 * Проверяет HTTP-статусы ответов и валидацию API.
 */
@SpringBootTest
public class BudgetControllerTest {

    @Autowired
    private WebApplicationContext context;

    @MockBean
    private BudgetService budgetService;
    private BudgetDto validBudgetDto;

    @BeforeEach
    void setUp() {
        // Настраиваем MockMvc c поддержкой Spring Security
        MockMvc mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();

        // Инициализируем RestAssured для использования с MockMvc
        RestAssuredMockMvc.mockMvc(mockMvc);
        RestAssuredMockMvc.basePath = "/api/budgets";

        User testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");

        Category testCategory = new Category();
        testCategory.setId(1L);
        testCategory.setName("Groceries");

        Budget testBudget = new Budget();
        testBudget.setId(1L);
        testBudget.setName("Monthly groceries budget");
        testBudget.setAmountLimit(new BigDecimal("500.00"));
        testBudget.setPeriodStart(LocalDate.of(2023, 10, 1));
        testBudget.setPeriodEnd(LocalDate.of(2023, 10, 31));
        testBudget.setUser(testUser);
        testBudget.setCategory(testCategory);

        validBudgetDto = new BudgetDto();
        validBudgetDto.setName("Monthly groceries budget");
        validBudgetDto.setAmountLimit(new BigDecimal("500.00"));
        validBudgetDto.setPeriodStart(LocalDate.of(2023, 10, 1));
        validBudgetDto.setPeriodEnd(LocalDate.of(2023, 10, 31));
        validBudgetDto.setCategoryId(1L);

        // Настройка моков для всех тестов
        when(budgetService.createBudget(any(BudgetDto.class))).thenReturn(testBudget);
        when(budgetService.getBudgetById(1L)).thenReturn(testBudget);
        when(budgetService.getBudgetById(999L)).thenThrow(new ResourceNotFoundException("Budget not found with id: 999"));
        when(budgetService.getCurrentUserBudgets()).thenReturn(List.of(testBudget));
        when(budgetService.isBudgetExceeded(1L)).thenReturn(false);
    }

    /**
     * Проверяет успешное создание бюджета.
     * Ожидаемый результат: HTTP 201 Created и корректные данные в ответе.
     */
    @Test
    void createBudget_WithValidData_Returns201Created() {
        given()
                .auth().with(user("testuser").roles("USER"))
                .contentType(ContentType.JSON)
                .body(validBudgetDto)
                .when()
                .post()
                .then()
                .statusCode(201)
                .body("id", equalTo(1))
                .body("name", equalTo("Monthly groceries budget"))
                .body("amountLimit", comparesEqualTo(500.00f));
    }

    /**
     * Проверяет обработку ошибки при отправке невалидных данных.
     * Ожидаемый результат: HTTP 400 Bad Request или 500 Internal Server Error с ошибками валидации.
     */
    @Test
    void createBudget_WithInvalidData_ReturnsError() {
        BudgetDto invalidDto = new BudgetDto();

        given()
                .auth().with(user("testuser").roles("USER"))
                .contentType(ContentType.JSON)
                .body(invalidDto)
                .when()
                .post()
                .then()
                .statusCode(anyOf(equalTo(400), equalTo(500)))
                .body("message", containsString("Validation failed"));
    }

    /**
     * Проверяет получение бюджета по ID.
     * Ожидаемый результат: HTTP 200 OK и корректные данные в ответе.
     */
    @Test
    void getBudgetById_ExistingId_Returns200Ok() {
        given()
                .auth().with(user("testuser").roles("USER"))
                .when()
                .get("/{id}", "1") // используем строку вместо числа
                .then()
                .statusCode(200)
                .body("id", equalTo(1))
                .body("name", equalTo("Monthly groceries budget"))
                .body("amountLimit", comparesEqualTo(500.00f));
    }

    /**
     * Проверяет обработку запроса на получение несуществующего бюджета.
     * Ожидаемый результат: HTTP 404 Not Found.
     */
    @Test
    void getBudgetById_NonExistingId_Returns404NotFound() {
        given()
                .auth().with(user("testuser").roles("USER"))
                .when()
                .get("/{id}", "999") // используем строку вместо числа
                .then()
                .statusCode(404);
    }

    /**
     * Проверяет получение всех бюджетов текущего пользователя.
     * Ожидаемый результат: HTTP 200 OK и список бюджетов в ответе.
     */
    @Test
    void getCurrentUserBudgets_Returns200OkWithBudgetsList() {
        given()
                .auth().with(user("testuser").roles("USER"))
                .when()
                .get()
                .then()
                .statusCode(200)
                .body("size()", equalTo(1))
                .body("[0].id", equalTo(1))
                .body("[0].name", equalTo("Monthly groceries budget"));
    }

    /**
     * Проверяет получение пустого списка бюджетов, если у пользователя их нет.
     * Ожидаемый результат: HTTP 200 OK и пустой список в ответе.
     */
    @Test
    void getCurrentUserBudgets_UserWithNoBudgets_Returns200OkWithEmptyList() {
        when(budgetService.getCurrentUserBudgets()).thenReturn(Collections.emptyList());

        given()
                .auth().with(user("testuser").roles("USER"))
                .when()
                .get()
                .then()
                .statusCode(200)
                .body("size()", equalTo(0));
    }

    /**
     * Проверяет получение статуса бюджета (превышен или нет).
     * Ожидаемый результат: HTTP 200 OK и корректные данные в ответе.
     */
    @Test
    void getBudgetStatus_ExistingId_Returns200OkWithStatus() {
        given()
                .auth().with(user("testuser").roles("USER"))
                .when()
                .get("/{id}/status", "1") // используем строку вместо числа
                .then()
                .statusCode(200)
                .body("budget.id", equalTo(1))
                .body("budget.name", equalTo("Monthly groceries budget"))
                .body("isExceeded", equalTo(false));
    }

    /**
     * Проверяет получение статуса для бюджета, который превышен.
     * Ожидаемый результат: HTTP 200 OK и флаг isExceeded = true.
     */
    @Test
    void getBudgetStatus_ExceededBudget_Returns200OkWithExceededFlag() {
        when(budgetService.isBudgetExceeded(1L)).thenReturn(true);

        given()
                .auth().with(user("testuser").roles("USER"))
                .when()
                .get("/{id}/status", "1") // используем строку вместо числа
                .then()
                .statusCode(200)
                .body("budget.id", equalTo(1))
                .body("isExceeded", equalTo(true));
    }

    /**
     * Проверяет обработку запроса статуса для несуществующего бюджета.
     * Ожидаемый результат: HTTP 404 Not Found.
     */
    @Test
    void getBudgetStatus_NonExistingId_Returns404NotFound() {
        given()
                .auth().with(user("testuser").roles("USER"))
                .when()
                .get("/{id}/status", "999") // используем строку вместо числа
                .then()
                .statusCode(404);
    }

    /**
     * Проверяет обработку запроса без аутентификации.
     * Ожидаемый результат: HTTP 401 Unauthorized, 403 Forbidden или 302 Found (редирект на страницу логина).
     */
    @Test
    void anyEndpoint_WithoutAuthentication_ReturnsUnauthorized() {
        // Сбрасываем настройки аутентификации
        RestAssuredMockMvc.reset();

        MockMvc mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
        RestAssuredMockMvc.mockMvc(mockMvc);
        RestAssuredMockMvc.basePath = "/api/budgets";

        given()
                .when()
                .get()
                .then()
                .statusCode(anyOf(equalTo(401), equalTo(403), equalTo(302)));
    }

    /**
     * Проверяет ответ при невалидном формате ID в URL.
     * Ожидаемый результат: HTTP 400 Bad Request или 500 Internal Server Error.
     */
    @Test
    void getBudgetById_InvalidIdFormat_ReturnsErrorStatus() {
        given()
                .auth().with(user("testuser").roles("USER"))
                .when()
                .get("/{id}", "invalid-id")
                .then()
                .statusCode(anyOf(equalTo(400), equalTo(500)));
    }
}