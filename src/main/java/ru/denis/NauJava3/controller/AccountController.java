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
import ru.denis.NauJava3.entity.Account;
import ru.denis.NauJava3.exception.BadRequestException;
import ru.denis.NauJava3.exception.ResourceNotFoundException;
import ru.denis.NauJava3.repository.AccountRepository;

import java.math.BigDecimal;
import java.util.List;

/**
 * REST контроллер для управления банковскими счетами.
 *
 * @see Account
 * @see AccountRepository
 */
@RestController
@RequestMapping("/api/accounts")
@Tag(name = "Account Controller", description = "API для работы со счетами пользователей")
@JsonIgnoreProperties({"transactions"})
public class AccountController {

    @Autowired
    private AccountRepository accountRepository;

    /**
     * Поиск счетов с использованием Criteria API.
     * Позволяет искать счета по email пользователя и диапазону баланса.
     * Параметры баланса являются опциональными.
     *
     * @param userEmail email пользователя
     * @param minBalance минимальный баланс (опционально)
     * @param maxBalance максимальный баланс (опционально)
     * @return список найденных счетов
     * @throws BadRequestException если email пустой или минимальный баланс больше максимального
     * @throws ResourceNotFoundException если счета не найдены
     */
    @Operation(
            summary = "Поиск счетов по email и балансу (Criteria API)",
            description = "Находит все счета пользователя с указанным email и балансом в заданном диапазоне, используя Criteria API"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Счета успешно найдены"),
            @ApiResponse(responseCode = "400", description = "Неверные параметры запроса"),
            @ApiResponse(responseCode = "404", description = "Счета не найдены")
    })
    @GetMapping("/search/byEmailAndBalanceUsedCriteria")
    public List<Account> findByEmailAndBalanceUsedCriteria(
            @RequestParam String userEmail,
            @RequestParam(required = false) BigDecimal minBalance,
            @RequestParam(required = false) BigDecimal maxBalance) {

        validateEmailAndBalance(userEmail, minBalance, maxBalance);

        List<Account> accounts = accountRepository.findUserAccountsByEmailAndBalanceRangeCriteria(
                userEmail, minBalance, maxBalance);

        validateAccountsNotEmpty(accounts, userEmail);

        return accounts;
    }

    /**
     * Поиск счетов по точным параметрам.
     * Все параметры являются обязательными.
     *
     * @param userEmail email пользователя
     * @param minBalance минимальный баланс
     * @param maxBalance максимальный баланс
     * @return список найденных счетов
     * @throws BadRequestException если email пустой или минимальный баланс больше максимального
     * @throws ResourceNotFoundException если счета не найдены
     */
    @Operation(
            summary = "Поиск счетов по email и балансу",
            description = "Находит все счета пользователя с указанным email и балансом в заданном диапазоне"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Счета успешно найдены"),
            @ApiResponse(responseCode = "400", description = "Неверные параметры запроса"),
            @ApiResponse(responseCode = "404", description = "Счета не найдены")
    })
    @GetMapping("/search/byEmailAndBalance")
    public List<Account> findByEmailAndBalance(
            @RequestParam String userEmail,
            @RequestParam BigDecimal minBalance,
            @RequestParam BigDecimal maxBalance) {

        validateEmailAndBalance(userEmail, minBalance, maxBalance);

        List<Account> accounts = accountRepository.findUserAccountsByEmailAndBalanceRange(
                userEmail, minBalance, maxBalance);

        validateAccountsNotEmpty(accounts, userEmail);

        return accounts;
    }

    /**
     * Проверяет корректность email и соотношения балансов.
     *
     * @param userEmail проверяемый email
     * @param minBalance минимальный баланс
     * @param maxBalance максимальный баланс
     * @throws BadRequestException если параметры некорректны
     */
    private void validateEmailAndBalance(String userEmail, BigDecimal minBalance, BigDecimal maxBalance) {
        if (userEmail == null || userEmail.trim().isEmpty()) {
            throw new BadRequestException("Email пользователя не может быть пустым");
        }

        if (minBalance != null && maxBalance != null && minBalance.compareTo(maxBalance) > 0) {
            throw new BadRequestException("Минимальный баланс не может быть больше максимального");
        }
    }

    /**
     * Проверяет, что список найденных счетов не пуст.
     *
     * @param accounts список счетов для проверки
     * @param userEmail email пользователя
     * @throws ResourceNotFoundException если список пуст
     */
    private void validateAccountsNotEmpty(List<Account> accounts, String userEmail) {
        if (accounts.isEmpty()) {
            throw new ResourceNotFoundException("Accounts", "userEmail", userEmail);
        }
    }
}
