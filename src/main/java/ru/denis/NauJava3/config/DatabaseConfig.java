package ru.denis.NauJava3.config;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import ru.denis.NauJava3.entity.Transaction;

import java.util.ArrayList;
import java.util.List;

/**
 * Конфигурация хранилища данных.
 * Создает и настраивает контейнер для хранения транзакций
 */
@Configuration
public class DatabaseConfig {

    /**
     * Создает singleton-контейнер для хранения транзакций
     * @return список для хранения транзакций
     */
    @Bean
    @Scope(value = BeanDefinition.SCOPE_SINGLETON)
    public List<Transaction> transactionContainer() {
        return new ArrayList<>();
    }
}
