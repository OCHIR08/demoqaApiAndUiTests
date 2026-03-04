package com.demoqa.api.account;

import com.demoqa.api.models.request.AccountModel;
import com.demoqa.api.models.response.LoginResponse;
import com.demoqa.api.services.AccountServices;
import com.demoqa.base.BaseApiTest;
import io.qameta.allure.Description;
import io.qameta.allure.Owner;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static com.demoqa.factory.UserFactory.*;
import static com.demoqa.factory.UserFactory.UserWithPassword;
import static com.demoqa.utils.ErrorMessages.*;

public class LoginTest extends BaseApiTest {
    private final AccountServices accountServices = new AccountServices();

//    ✅ Успешный вход
    @Test
    @Tag("api")
    @Owner("Ermoshkaev")
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("Успешная аутентификация с использованием действительных учетных данных")
    @Description("Проверка: , статус 200")
    void successLoginTest() {
        // Arrange
        AccountModel credentials = loginIvan();
        // Act
        LoginResponse response = accountServices.login(credentials);
        // Assert
        accountServices.verifyLoginResponse(response, credentials.getUserName());
    }

//    ✅ Возвращаются все поля
    @Test
    @Tag("api")
    @Owner("Ermoshkaev")
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("Проверка схемы ответа на запрос авторизации")
    void testLoginSchema() {
        // Arrange
        AccountModel credentials = loginIvan();
        // Assert
        accountServices.validateLoginSchema(credentials);
    }


//     ❌ Несуществующий пользователь
    @Test
    @Tag("api")
    @Owner("Ermoshkaev")
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("Аутентификация не существующего пользователя")
    void unSuccessLoginTest(){
        // Arrange
        AccountModel credentials = userNotHave();
        // Act
        Response response = accountServices.tryLoginRaw(credentials);
        // Assert
        accountServices.verifyLoginError(response,"1207");
    }

// 🔹 ==================== НЕГАТИВНЫЕ ТЕСТЫ: ПАРОЛЬ ====================

//    ❌ Неверный пароль
    @Test
    @Tag("api")
    @Owner("Ermoshkaev")
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("Авторизация с неверным паролем → 404")
    void loginWithWrongPassword() {
        // Arrange
        AccountModel credentials = UserWithPassword("WrongPasswowererd123!");
        // Act
        Response response = accountServices.tryLoginRaw(credentials);
        // Assert
        accountServices.verifyErrorMessage(
                response,
                CODE_1207,
                MSG_USER_NOT_FOUND,
                404
        );
    }

// ❌ null username
    @Test
    @Tag("api")
    @Owner("Ermoshkaev")
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("Авторизация с null username → 400")
    void loginWithNullUsername() {
        // Arrange
        AccountModel credentials = UserWithUsername(null);
        // Act
        Response response = accountServices.tryLoginRaw(credentials);
        // Assert
        accountServices.verifyErrorMessage(
                response,
                CODE_1200,
                MSG_CREDENTIALS_REQUIRED,
                400
        );
    }

//    ❌ Пустой username
    @Test
    @Tag("api")
    @Owner("Ermoshkaev")
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("Авторизация с пустым username")
    void loginWithEmptyUsername(){
        // Arrange
        AccountModel credentials = UserWithUsername("");
        // Act
        Response response = accountServices.tryLoginRaw(credentials);
        // Assert
        accountServices.verifyErrorMessage(
                response,
                CODE_1200,
                MSG_CREDENTIALS_REQUIRED,
                400
        );
    }

//     ❌ Пустой пароль
    @Test
    @Tag("api")
    @Owner("Ermoshkaev")
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("Авторизация с пустым паролем")
    void loginWithEmptyPassword(){
        // Arrange
        AccountModel credentials = UserWithPassword("");
        // Act
        Response response = accountServices.tryLoginRaw(credentials);
        // Assert
        accountServices.verifyErrorMessage(
                response,
                CODE_1200,
                MSG_CREDENTIALS_REQUIRED,
                400
        );
    }

}
