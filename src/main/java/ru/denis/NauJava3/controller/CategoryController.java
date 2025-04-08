package ru.denis.NauJava3.controller;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.denis.NauJava3.entity.Category;
import ru.denis.NauJava3.entity.enums.OperationType;
import ru.denis.NauJava3.exception.BadRequestException;
import ru.denis.NauJava3.exception.ResourceNotFoundException;
import ru.denis.NauJava3.repository.CategoryRepository;

import java.util.List;

/**
 * REST контроллер для управления категориями финансовых операций.
 *
 * @see Category
 * @see CategoryRepository
 */
@RestController
@RequestMapping("/api/categories")
@Tag(name = "Category Controller", description = "API для работы с категориями")
@JsonIgnoreProperties({"transactions", "budgets"})
public class CategoryController {

    @Autowired
    private CategoryRepository categoryRepository;

    /**
     * Поиск категорий с использованием Criteria API.
     * Позволяет искать категории по типу операции, цветовому коду и части названия.
     * Все параметры являются опциональными.
     *
     * @param type тип операции (INCOME/EXPENSE)
     * @param colorCode цветовой код в формате #RRGGBB или #RGB
     * @param namePart часть названия категории (минимум 2 символа)
     * @return список найденных категорий
     * @throws BadRequestException если формат цветового кода неверный или название слишком короткое
     * @throws ResourceNotFoundException если категории не найдены
     */
    @Operation(
            summary = "Поиск категорий по типу, цвету и названию (Criteria API)",
            description = "Находит категории по заданным параметрам с использованием Criteria API"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Категории успешно найдены"),
            @ApiResponse(responseCode = "400", description = "Неверный формат параметров"),
            @ApiResponse(responseCode = "404", description = "Категории не найдены")
    })
    @GetMapping("/search/byTypeAndColorAndNameUsedCriteria")
    public List<Category> findByTypeAndColorAndNameUsedCriteria(
            @RequestParam(required = false) OperationType type,
            @RequestParam(required = false) String colorCode,
            @RequestParam(required = false) String namePart) {

        validateColorCodeFormat(colorCode);
        validateNamePart(namePart);

        List<Category> categories = categoryRepository.findByTypeAndColorCodeAndNameContainingCriteria(
                type, colorCode, namePart);

        validateCategoriesNotEmpty(categories, type, colorCode, namePart);

        return categories;
    }

    /**
     * Поиск категорий по точным параметрам.
     * Все параметры являются обязательными.
     *
     * @param type тип операции (INCOME/EXPENSE)
     * @param colorCode цветовой код в формате #RRGGBB или #RGB
     * @param namePart часть названия категории (минимум 2 символа)
     * @return список найденных категорий
     * @throws BadRequestException если формат цветового кода неверный или название слишком короткое
     * @throws ResourceNotFoundException если категории не найдены
     */
    @Operation(
            summary = "Поиск категорий по типу, цвету и названию",
            description = "Находит категории по заданным параметрам"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Категории успешно найдены"),
            @ApiResponse(responseCode = "400", description = "Неверный формат параметров"),
            @ApiResponse(responseCode = "404", description = "Категории не найдены")
    })
    @GetMapping("/search/byTypeAndColorAndName")
    public List<Category> findByTypeAndColorAndName(
            @RequestParam OperationType type,
            @RequestParam String colorCode,
            @RequestParam String namePart) {

        validateColorCodeFormat(colorCode);
        validateNamePart(namePart);

        List<Category> categories = categoryRepository.findByTypeAndColorCodeAndNameContaining(
                type, colorCode, namePart);

        validateCategoriesNotEmpty(categories, type, colorCode, namePart);

        return categories;
    }

    /**
     * Проверяет корректность формата цветового кода.
     *
     * @param colorCode проверяемый цветовой код
     * @throws BadRequestException если формат кода неверный
     */
    private void validateColorCodeFormat(String colorCode) {
        if (colorCode != null && !colorCode.isEmpty()) {
            if (!colorCode.matches("^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$")) {
                throw new BadRequestException("Неверный формат цветового кода. Используйте формат #RRGGBB или #RGB");
            }
        }
    }

    /**
     * Проверяет минимальную длину части названия категории.
     *
     * @param namePart проверяемая часть названия
     * @throws BadRequestException если длина названия меньше 2 символов
     */
    private void validateNamePart(String namePart) {
        if (namePart != null && namePart.trim().length() < 2) {
            throw new BadRequestException("Часть названия категории должна содержать минимум 2 символа");
        }
    }

    /**
     * Проверяет, что список найденных категорий не пуст.
     *
     * @param categories список категорий для проверки
     * @param type тип операции
     * @param colorCode цветовой код
     * @param namePart часть названия
     * @throws ResourceNotFoundException если список пуст
     */
    private void validateCategoriesNotEmpty(List<Category> categories, OperationType type,
                                            String colorCode, String namePart) {
        if (categories.isEmpty()) {
            StringBuilder details = new StringBuilder("Категории не найдены для параметров: ");
            if (type != null) details.append("тип=").append(type).append(", ");
            if (colorCode != null) details.append("цвет=").append(colorCode).append(", ");
            if (namePart != null) details.append("название=").append(namePart);

            throw new ResourceNotFoundException("Categories", "search parameters", details.toString());
        }
    }
}
