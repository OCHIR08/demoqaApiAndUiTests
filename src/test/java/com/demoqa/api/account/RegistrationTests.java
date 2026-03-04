package com.demoqa.api.account;

import com.demoqa.api.models.request.AccountModel;
import com.demoqa.api.models.response.GetTokenResponse;
import com.demoqa.api.models.response.registration.RegistrationResponse;
import com.demoqa.api.services.AccountServices;
import io.qameta.allure.Description;
import io.qameta.allure.Owner;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static com.demoqa.factory.UserFactory.*;
import static com.demoqa.utils.ErrorMessages.*;

public class RegistrationTests {
    private final AccountServices accountServices = new AccountServices();

//    ✅ Базовая регистрация
    @Test
    @Tag("api")
    @Tag("positive")
    @Owner("Ermoshkaev")
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("Регистрация нового пользователя с валидными данными")
    @Description("Проверка: userID, username, пустой список книг, статус 201")
    void registrationWithValidData() {
        // Arrange
        AccountModel user = createValidUser();
        // Act
        RegistrationResponse response = accountServices.registrationNew(user);
        // Assert
        accountServices.verifyRegistrationSuccess(response, user.getUserName());
    }

//    ✅ Мин. пароль
    @Test
    @Tag("api")
    @Tag("positive")
    @Owner("Ermoshkaev")
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("Регистрация с минимально допустимым паролем")
    @Description("Проверка: userID, username, пустой список книг, статус 201")
    void registrationWithMinimalValidPassword() {
        // Arrange
        AccountModel user = UserWithPassword("Aa12345!");
        // Act
        RegistrationResponse response = accountServices.registrationNew(user);
        // Assert
        accountServices.verifyRegistrationSuccess(response, user.getUserName());
    }

//    ✅Валидация ответа
    @Test
    @Tag("api")
    @Tag("positive")
    @Owner("Ermoshkaev")
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("validateRegistrationResponse")
    @Description("Проверка: схемы и типов ответа, обязателных полей, статус 200")
    void validResponseRegistration(){
        // Arrange
        AccountModel user = createValidUser();
        // Assert
        accountServices.validateRegistrationSchema(user);
    }

    @Test
    @Tag("api")
    @Tag("positive")
    @Owner("Ermoshkaev")
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("Удаление пользователя")
    @Description("Проверка: , статус 401")
    void deleteUSER(){
        // Arrange
        AccountModel user = createValidUser();
        // Act
        RegistrationResponse response = accountServices.registrationNew(user);
        GetTokenResponse getTokenResponse = accountServices.getToken(user);
        accountServices.deleteUser(response.getUserId(),getTokenResponse.getToken());
        Response infoResponse = accountServices.info(response.getUserId(),getTokenResponse.getToken());
        // Assert
        accountServices.verifyErrorMessage(
                infoResponse,
                CODE_1207,
                MSG_USER_NOT_FOUND,
                401
        );
    }



    // 🔹 ==================== НЕГАТИВНЫЕ ТЕСТЫ: ПАРОЛЬ ====================


//    ❌ Регистрация нового пользователя c невалидным паролем
    @Test
    @Tag("api")
    @Tag("negative")
    @Owner("Ermoshkaev")
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("Регистрация нового пользователя c невалидным паролем > 400")
    @Description("Проверка: кода ошибки,сообщения ошибки,статус 400")
    void registrationNoValidPassword(){
        // Arrange
        AccountModel user = UserWithPassword("a123456");
        // Act
        Response response =accountServices.tryRegistrationRaw(user);
        // Assert
        accountServices.verifyErrorMessage(
                response,
                CODE_1300,
                MSG_PASSWORD_TOO_SHORT,
                400
        );
    }

//     ❌ < 8 символов
    @Test
    @Tag("api")
    @Tag("negative")
    @Owner("Ermoshkaev")
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("Регистрация с паролем короче 8 символов > 400")
    @Description("Проверка: кода ошибки,сообщения ошибки,статус 400")
    void registrationWithShortPassword() {
        // Arrange
        AccountModel user = UserWithPassword("a1");
        // Act
        Response response = accountServices.tryRegistrationRaw(user);
        // Assert
        accountServices.verifyErrorMessage(
                response,
                CODE_1300,
                MSG_PASSWORD_TOO_SHORT,
                400
        );
    }

//         ❌ Пустой пароль
    @Test
    @Tag("api")
    @Tag("negative")
    @Owner("Ermoshkaev")
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("Регистрация с пустым паролем > 400")
    @Description("Проверка: кода ошибки,сообщения ошибки,статус 400")
    void registrationWithEmptyPassword(){
        // Arrange
        AccountModel user = UserWithPassword("");
        // Act
        Response response = accountServices.tryRegistrationRaw(user);
        // Assert
        accountServices.verifyErrorMessage(
                response,
                CODE_1200,
                MSG_CREDENTIALS_REQUIRED,
                400
        );
    }

//         ❌ Пустой userName
    @Test
    @Tag("api")
    @Tag("negative")
    @Owner("Ermoshkaev")
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("Регистрация с пустым userName > 400")
    @Description("Проверка: кода ошибки,сообщения ошибки,статус 400")
    void registrationWithEmptyUserName(){
        // Arrange
        AccountModel user = UserWithUsername("");
        // Act
        Response response = accountServices.tryRegistrationRaw(user);
        // Assert
        accountServices.verifyErrorMessage(
                response,
                CODE_1200,
                MSG_CREDENTIALS_REQUIRED,
                400
        );
    }

//     ❌ Дубликат
    @Test
    @Tag("api")
    @Tag("negative")
    @Owner("Ermoshkaev")
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("Повторная регистрация существующего пользователя > 406")
    @Description("Проверка: кода ошибки,сообщения ошибки,статус 406")
    void registration_duplicateUser_error406(){
        // Arrange
        AccountModel user = createValidUser();
        // Act
        accountServices.registrationNew(user);
        Response response = accountServices.tryRegistrationRaw(user);
        // Assert
        accountServices.verifyErrorMessage(
                response,
                CODE_1204,
                MSG_USER_EXISTS,
                406
        );
    }


}
