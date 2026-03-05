package com.demoqa.api.account;

import com.demoqa.api.models.request.AccountModel;
import com.demoqa.api.models.response.LoginResponse;
import com.demoqa.api.services.AccountServices;
import com.demoqa.utils.JwtUtils;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static com.demoqa.factory.UserFactory.*;

public class GenerateTokenTests {
    private final AccountServices accountServices = new AccountServices();

    //    ✅ Positive-проверки
    @Test
    @Tag("api")
    @Tag("positive")
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("Login: валидные credentials → 200 + JWT + expires")
    void login_validCredentials_success() {
        // Arrange
        AccountModel credentials = loginIvan();
        // Act
        LoginResponse response = accountServices.login(credentials);
        // Assert
        accountServices.verifyLoginResponse(response, credentials.getUserName());
        Assertions.assertTrue(
                JwtUtils.isValidFormat(response.getToken()),
                "Token должен быть валидным JWT форматом"
        );
    }

    @Test
    @Tag("api")
    @Tag("positive")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Login: токен содержит userId и username в payload")
    void login_tokenPayload_containsUserData() {
        // Arrange
        AccountModel user = createValidUser();
        // Act
        String tokenUsername = accountServices.extractUsernameFromToken(user);
        // Assert
        Assertions.assertEquals(user.getUserName(), tokenUsername);
    }

//    ❌ Negative-проверки


//    🔐 Security
    @Test
    @Tag("api")
    @Tag("security")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Login: brute-force защита → блокировка после 5 неудачных попыток")
        void login_bruteForce_protection() {
        // Arrange
        AccountModel validUser = createValidUser();
        AccountModel wrongCredentials = UserWithPassword("WrongPass123!");
        accountServices.registrationNew(validUser); // регистрируем, чтобы пользователь существовал
        // Act: 5 неудачных попыток + 1 проверочная
        accountServices.attemptFailedLogins(wrongCredentials, 5);
        Response afterLimitResponse = accountServices.tryAuthRaw(wrongCredentials);
        // Assert
        accountServices.assertBruteForceProtection(afterLimitResponse);
    }

    @Test
    @Tag("api")
    @Tag("security")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Login: JWT payload не содержит пароль или другие чувствительные данные")
    void login_token_noPasswordInPayload() {
        // Arrange
        AccountModel credentials = createValidUser();
        // Act
        accountServices.registrationNew(credentials);
        String token = accountServices.getToken(credentials).getToken();
        // Assert
        Assertions.assertTrue(
                JwtUtils.doesNotContainPassword(token),
                "JWT payload не должен содержать поля: password, pass, pwd"
        );
    }
}
