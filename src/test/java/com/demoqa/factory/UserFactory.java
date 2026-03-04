package com.demoqa.factory;

import com.demoqa.api.models.request.AccountModel;
import com.demoqa.config.Config;
import com.demoqa.utils.ErrorMessages;
import com.github.javafaker.Faker;

/**
 * Factory для создания тестовых данных пользователя
 * Использует паттерн Test Data Factory / Object Mother
 */
public class UserFactory {

    private static final Faker faker = new Faker();

    // 🔹 Приватный конструктор — класс только со статическими методами
    private UserFactory() {}

    /**
     *  Существующий пользователь Ivan
     */
    public static AccountModel loginIvan(){
        return AccountModel.builder()
                .userName(Config.loginIvan())
                .password(Config.passwordIvan())
                .build();
    }

    /**
     *  Не существующий пользователь
     */
    public static AccountModel userNotHave(){
        return AccountModel.builder()
                .userName("UserNotHave")
                .password("11111")
                .build();
    }

    /**
     * Создаёт валидного пользователя со случайным username
     */
    public static AccountModel createValidUser() {
        return AccountModel.builder()
                .userName(faker.name().username())
                .password(ErrorMessages.VALID_PASSWORD)
                .build();
    }

    /**
     * Создаёт пользователя с заданным паролем
     */
    public static AccountModel UserWithPassword(String password) {
        return AccountModel.builder()
                .userName(faker.name().username())
                .password(password)
                .build();
    }

    /**
     * Создаёт пользователя с заданным username
     */
    public static AccountModel UserWithUsername(String username) {
        return AccountModel.builder()
                .userName(username)
                .password(ErrorMessages.VALID_PASSWORD)
                .build();
    }

    /**
     * Создаёт пользователя с заданными username и password
     */
    public static AccountModel createUser(String username, String password) {
        return AccountModel.builder()
                .userName(username)
                .password(password)
                .build();
    }

    /**
     * Создаёт пользователя с полностью кастомными данными
     */
    public static AccountModel createUserCustom(String username, String password, String... extra) {
        return AccountModel.builder()
                .userName(username)
                .password(password)
                .build();
    }

    /**
     * Генерирует уникальный username (с timestamp для избежания коллизий)
     */
    public static String generateUniqueUsername() {
        return faker.name().username() + "_" + System.currentTimeMillis();
    }

    /**
     * Генерирует валидный пароль по требованиям DemoQA
     */
    public static String generateValidPassword() {
        return ErrorMessages.VALID_PASSWORD;
    }

    /**
     * Генерирует невалидный пароль (короче 8 символов)
     */
    public static String generateShortPassword() {
        return "Aa1!";
    }

    /**
     * Генерирует пароль без заглавной буквы
     */
    public static String generatePasswordNoUppercase() {
        return "aa123456!";
    }

    /**
     * Генерирует пароль без строчной буквы
     */
    public static String generatePasswordNoLowercase() {
        return "AA123456!";
    }

    /**
     * Генерирует пароль без цифры
     */
    public static String generatePasswordNoDigit() {
        return "Aa!@#$%^";
    }

    /**
     * Генерирует пароль без спецсимвола
     */
    public static String generatePasswordNoSpecial() {
        return "Aa123456";
    }
}