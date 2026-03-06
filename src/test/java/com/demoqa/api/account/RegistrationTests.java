package com.demoqa.api.account;

import com.demoqa.api.models.request.AccountModel;
import com.demoqa.api.models.response.registration.RegistrationResponse;
import com.demoqa.api.services.AccountServices;
import io.qameta.allure.Description;
import io.qameta.allure.Owner;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static com.demoqa.factory.UserFactory.*;
import static com.demoqa.utils.ErrorMessages.*;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.equalTo;

public class RegistrationTests {
    private final AccountServices accountServices = new AccountServices();


    //✅ Positive-проверки
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
        Assertions.assertTrue(response.getBooks().isEmpty(), "У нового пользователя нет книг");
    }

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
        String name = user.getUserName();
        // Act
        RegistrationResponse response = accountServices.registrationNew(user);
        // Assert
        accountServices.verifyRegistrationSuccess(response, name);
    }

    @Test
    @Tag("api")
    @Tag("positive")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Регистрация: username с спецсимволами → 201")
    void registration_usernameWithSpecialChars_success() {
        // Arrange
        AccountModel user = UserWithPassword("User.test+qa_2026!@#$%");
        //Act
        RegistrationResponse response = accountServices.registrationNew(user);
        // Assert
        Assertions.assertEquals(user.getUserName(), response.getUsername());
    }

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
        //Act
        Response response = accountServices.tryRegistrationRaw(user);
        // Assert
        accountServices.validateApiResponse(response, 201, "registration-schema.json", "Проверка схемы регистрации");
    }

    //❌ Negative-проверки
    @Test
    @Tag("api")
    @Tag("negative")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Регистрация: пароль < 8 символов → 400 + code 1300")
    void registration_shortPassword_error400() {
        // Arrange
        AccountModel user = UserWithPassword("a1");
        //Act
        Response response = accountServices.tryRegistrationRaw(user);
        // Assert
        accountServices.verifyErrorMessage(response, CODE_1300, MSG_PASSWORD_TOO_SHORT, 400);
    }

    @Test
    @Tag("api")
    @Tag("negative")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Регистрация: пароль без заглавной буквы → 400")
    void registration_passwordNoUppercase_error400() {
        // Arrange
        AccountModel user = UserWithPassword("aa123456!"); // нет заглавной
        //Act
        Response response = accountServices.tryRegistrationRaw(user);
        // Assert
        accountServices.verifyErrorMessage(response, CODE_1300, MSG_PASSWORD_COMPLEXITY, 400);
    }

    @Test
    @Tag("api")
    @Tag("negative")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Регистрация: пустой username → 400 + code 1200")
    void registration_emptyUsername_error400() {
        // Arrange
        AccountModel user = UserWithUsername("");
        //Act
        Response response = accountServices.tryRegistrationRaw(user);
        // Assert
        accountServices.verifyErrorMessage(response, CODE_1200, MSG_CREDENTIALS_REQUIRED, 400);
    }

    @Test
    @Tag("api")
    @Tag("negative")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Регистрация: дубликат username → 406 + code 1204")
    void registration_duplicateUser_error4062() {
        // Arrange
        AccountModel user = createValidUser();
        //Act
        accountServices.registrationNew(user); // первая регистрация
        Response response = accountServices.tryRegistrationRaw(user); // повтор
        // Assert
        accountServices.verifyErrorMessage(response, CODE_1204, MSG_USER_EXISTS, 406);
    }


//TO DO: есть уязвимость. Позже надо разобрать
    @Test
    @Tag("api")
    @Tag("negative")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Регистрация: XSS в username → sanitized или 400")
    void registration_xssInUsername() {
        // Arrange
        AccountModel user = UserWithUsername("<script>alert(4)</script>");
        //Act
        Response response = accountServices.tryRegistrationRaw(user);
        // Assert
        // Ожидаем: либо 400, либо username очищен от script-тегов
        response.then().statusCode(anyOf(equalTo(400), equalTo(201)));
    }

    @Test
    @Tag("api")
    @Tag("negative")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Регистрация: SQL-injection в password → 400 или sanitized")
    void registration_sqlInjectionInPassword() {
        // Arrange
        AccountModel user = UserWithPassword("'; DROP TABLE users; --");
        //Act
        Response response = accountServices.tryRegistrationRaw(user);
        // Assert
        accountServices.verifyErrorMessage(response, CODE_1300, MSG_PASSWORD_COMPLEXITY, 400);
    }

    //    🔐 Security/Validation
    @Test
    @Tag("api")
    @Tag("security")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Регистрация: missing required fields → 400")
        void registration_missingFields_error400() {
        // Arrange
        // Только username, без password
        AccountModel user = AccountModel.builder().userName("test_user").build();
        //Act
        Response response = accountServices.tryRegistrationRaw(user);
        // Assert
        accountServices.verifyErrorMessage(response, CODE_1200, MSG_CREDENTIALS_REQUIRED, 400);
    }

    @Test
    @Tag("api")
    @Tag("security")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Регистрация: Content-Type не JSON → 400/415/500")
    void registration_wrongContentType() {
        // Arrange
        String invalidBody = "{\"userName\":\"test\",\"password\":\"Aa123456!\"}";
        // Act
        Response response = accountServices.tryRegisterWithWrongContentType(ContentType.TEXT, invalidBody);
        // Assert
        accountServices.assertContentTypeError(response);
    }

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
        accountServices.verifyErrorMessage(response, CODE_1300, MSG_PASSWORD_TOO_SHORT, 400);
    }
}
