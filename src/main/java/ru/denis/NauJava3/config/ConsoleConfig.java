package ru.denis.NauJava3.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.denis.NauJava3.console.CommandProcessor;

import java.util.Scanner;

/**
 * Конфигурация консольного интерфейса.
 * Обеспечивает работу с пользовательским вводом через консоль
 */
@Configuration
public class ConsoleConfig {

    /** Процессор команд для обработки пользовательского ввода */
    @Autowired
    private CommandProcessor commandProcessor;

    /** Конфигурация приложения, содержащая основные настройки и метаданные */
    @Autowired
    private AppConfig appConfig;

    /**
     * Создает и настраивает обработчик консольных команд
     * @return CommandLineRunner для обработки консольного ввода
     */
    @Bean
    public CommandLineRunner commandLineRunner() {
        return args -> {
            try (Scanner scanner = new Scanner(System.in)) {

                // Информация о приложении
                System.out.println("=======================================");
                System.out.println("Application: " + appConfig.getAppName());
                System.out.println("Version: " + appConfig.getAppVersion());
                System.out.println("=======================================");

                System.out.println("Финансовый менеджер запущен. Введите 'help' для списка команд или 'exit' для выхода.");

                while (true) {
                    System.out.print("> ");
                    String input = scanner.nextLine();

                    if ("exit".equalsIgnoreCase(input.trim())) {
                        System.out.println("Программа завершена");
                        break;
                    }

                    commandProcessor.processCommand(input);
                }
            }
        };
    }
}
