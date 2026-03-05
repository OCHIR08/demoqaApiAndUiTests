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
     *  userId non-existent-uuid
     */
    public static String nonExistentUuid(){
        return "yyyyeeee-test-test-test-123456789012";
    }

    public static String validUserIdIvan() { return  "9e7631f0-c898-43e6-afe3-f4699118feba"; }

    /**
     *  non valid token
     */
    public static String nonValidToken(){
        return "nonValidTokennonValidTokennonValidToken.nonValidTokennonValidTokennonValidTokennonValidTokennonValidToken.nonValidTokennonValidTokennonValidToken";
    }
}