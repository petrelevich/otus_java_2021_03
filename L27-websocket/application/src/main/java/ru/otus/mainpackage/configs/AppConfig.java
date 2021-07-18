package ru.otus.mainpackage.configs;

public class AppConfig {

    private final String message;

    public AppConfig(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "ApplicationConfig{" +
                "message='" + message + '\'' +
                '}';
    }
}
