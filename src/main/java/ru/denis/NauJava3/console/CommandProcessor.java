package ru.denis.NauJava3.console;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.denis.NauJava3.service.TransactionService;

import java.math.BigDecimal;

/**
 * Обработчик консольных команд пользователя
 */
@Component
public class CommandProcessor {
    private final TransactionService financeService;

    @Autowired
    public CommandProcessor(TransactionService financeService) {
        this.financeService = financeService;
    }

    /**
     * Обрабатывает введенную пользователем команду
     * @param input строка с командой
     */
    public void processCommand(String input) {
        String[] parts = input.trim().split("\\s+", 4);

        try {
            switch (parts[0].toLowerCase()) {
                case "income" -> {
                    if (parts.length >= 4) {
                        financeService.addIncome(
                                new BigDecimal(parts[1]),
                                parts[2],
                                parts[3]
                        );
                        System.out.println("Доход успешно добавлен");
                    } else {
                        System.out.println("Использование: income <сумма> <категория> <описание>");
                    }
                }
                case "expense" -> {
                    if (parts.length >= 4) {
                        financeService.addExpense(
                                new BigDecimal(parts[1]),
                                parts[2],
                                parts[3]
                        );
                        System.out.println("Расход успешно добавлен");
                    } else {
                        System.out.println("Использование: expense <сумма> <категория> <описание>");
                    }
                }
                case "total" -> {
                    System.out.println("Общий доход: " + financeService.getTotalIncome());
                    System.out.println("Общий расход: " + financeService.getTotalExpense());
                }
                case "help" -> {
                    System.out.println("Доступные команды:");
                    System.out.println("income <сумма> <категория> <описание> - добавить доход");
                    System.out.println("expense <сумма> <категория> <описание> - добавить расход");
                    System.out.println("total - показать общие суммы доходов и расходов");
                    System.out.println("exit - выход из программы");
                }
                default -> System.out.println("Неизвестная команда. Введите 'help' для списка команд");
            }
        } catch (NumberFormatException e) {
            System.out.println("Ошибка: сумма должна быть числом");
        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }
}
