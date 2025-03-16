package ru.denis.NauJava3.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Конфигурация приложения.
 * Содержит основные настройки приложения, загружаемые из application.properties
 */
@Configuration
public class AppConfig {

    /** Название приложения */
    @Value("${app.name}")
    private String appName;

    /** Версия приложения */
    @Value("${app.version:0.0.1}")
    private String appVersion;

    /** @return название приложения */
    public String getAppName() {
        return appName;
    }

    /** @return версию приложения */
    public String getAppVersion() {
        return appVersion;
    }
}
