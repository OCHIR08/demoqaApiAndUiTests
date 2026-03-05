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

    //✅ Positive-проверки
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

    @Test
    @Tag("api")
    @Owner("Ermoshkaev")
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("Проверка схемы ответа на запрос авторизации")
    void testLoginSchema() {
        // Arrange
        AccountModel credentials = loginIvan();
        //Act
        Response response = accountServices.tryLoginRaw(credentials);
        // Assert
        accountServices.validateApiResponse(
                response,
                200,
                "login-schema.json",
                "Проверка схемы авторизации"
        );
    }


    //❌ Negative-проверки
    @Test
    @Tag("api")
    @Owner("Ermoshkaev")
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("Login: несуществующий пользователь → 404 + code 1207")
    void login_nonExistentUser_error404(){
        // Arrange
        AccountModel credentials = userNotHave();
        // Act
        Response response = accountServices.tryAuthRaw(credentials);
        // Assert
        accountServices.verifyLoginError(response,"1207");
    }

    @Test
    @Tag("api")
    @Owner("Ermoshkaev")
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("Login: неверный пароль → 404 + code 1207")
    void login_wrongPassword_error404() {
        // Arrange
        AccountModel credentials = UserWithPassword("WrongPasswowererd123!");
        // Act
        Response response = accountServices.tryAuthRaw(credentials);
        // Assert
        accountServices.verifyErrorMessage(response, CODE_1207, MSG_USER_NOT_FOUND, 404);
    }

    @Test
    @Tag("api")
    @Owner("Ermoshkaev")
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("Login: null username → 400 + code 1200")
    void login_nullUsername_error400() {
        // Arrange
        AccountModel credentials = UserWithUsername(null);
        // Act
        Response response = accountServices.tryAuthRaw(credentials);
        // Assert
        accountServices.verifyErrorMessage(response, CODE_1200, MSG_CREDENTIALS_REQUIRED, 400);
    }

    @Test
    @Tag("api")
    @Owner("Ermoshkaev")
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("Login: пустой password → 400 + code 1200")
    void login_emptyPassword_error400(){
        // Arrange
        AccountModel credentials = UserWithUsername("");
        // Act
        Response response = accountServices.tryAuthRaw(credentials);
        // Assert
        accountServices.verifyErrorMessage(response, CODE_1200, MSG_CREDENTIALS_REQUIRED, 400);
    }

    @Test
    @Tag("api")
    @Owner("Ermoshkaev")
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("Авторизация с пустым паролем")
    void loginWithEmptyPassword(){
        // Arrange
        AccountModel credentials = UserWithPassword("");
        // Act
        Response response = accountServices.tryAuthRaw(credentials);
        // Assert
        accountServices.verifyErrorMessage(
                response,
                CODE_1200,
                MSG_CREDENTIALS_REQUIRED,
                400
        );
    }

}
