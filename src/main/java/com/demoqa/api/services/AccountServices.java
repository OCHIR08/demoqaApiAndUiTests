package com.demoqa.api.services;

import com.demoqa.api.clients.AccountClient;
import com.demoqa.api.models.response.AccountErrorResponse;
import com.demoqa.api.models.response.GetTokenResponse;
import com.demoqa.api.models.response.registration.RegistrationResponse;
import com.demoqa.api.models.request.AccountModel;
import com.demoqa.api.models.response.LoginResponse;
import com.demoqa.config.Config;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;

import java.util.UUID;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

public class AccountServices {
    private final AccountClient client  = new AccountClient();

    @Step("Авторизация пользователя")
    public LoginResponse login(AccountModel credentials){
        return client.login(credentials)
                .then()
                .statusCode(200)
                .extract()
                .as(LoginResponse.class);
    }

    @Step("Регистрация")
    public RegistrationResponse registrationNew(AccountModel credentials) {
        return client.registration(credentials)
                .then()
                .statusCode(201)
                .extract()
                .as(RegistrationResponse.class);
    }

    @Step("Получить данные клиента")
    public Response info(String userId, String token){
        return client.getInfoAccount(userId,token);
    }

    @Step("Генерация JWT токена для авторизации")
    public GetTokenResponse getToken(AccountModel credentials){
        return client.generateToken(credentials)
                .then()
                .statusCode(200)  // 🔥 Валидация статуса
                .extract()
                .as(GetTokenResponse.class);
    }


    @Step("deleteUser")
    public Response deleteUser(String userId, String token){
        return client.deleteAccount(userId,token);
    }

    //============проверки============ //


    @Step("Проверка схемы авторизации")
    public void validateLoginSchema(AccountModel credentials) {
        Response response = client.login(credentials);

        response.then()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("login-schema.json"));
    }

    @Step("Проверка схемы авторизации")
    public void validateRegistrationSchema(AccountModel credentials) {
        Response response = client.registration(credentials);

        response.then()
                .statusCode(201)
                .body(matchesJsonSchemaInClasspath("registration-schema.json"));
    }


    @Step("Проверка полей ответа при входе в систему(Token/UserId/Username/Expires)")
    public void verifyLoginResponse(LoginResponse response, String expectedUsername) {
        Assertions.assertAll("Валидация полей LoginResponse",
                () -> Assertions.assertNotNull(response.getToken(), "Токен не сгенерирован"),
                () -> Assertions.assertNotNull(response.getUserId(), "UserId отсутствует"),
                () -> Assertions.assertEquals(expectedUsername, response.getUsername(), "Имя пользователя не совпадает"),
                () -> Assertions.assertNotNull(response.getExpires(), "Поле 'expires' пустое")
        );
    }

    @Step("Валидация успешного ответа регистрации")
    public void verifyRegistrationSuccess(RegistrationResponse response, String expectedUsername) {
        Assertions.assertAll("Валидация RegistrationResponse",
                () -> Assertions.assertNotNull(response.getUserId(), "userID не должен быть null"),
                () -> Assertions.assertTrue(isValidUuid(response.getUserId()),
                        "userID должен быть валидным UUID: " + response.getUserId()),
                () -> Assertions.assertEquals(expectedUsername, response.getUsername(),
                        "username в ответе не совпадает с запросом"),
                () -> Assertions.assertNotNull(response.getBooks(), "books не должен быть null"),
                () -> Assertions.assertTrue(response.getBooks().isEmpty(),
                        "У нового пользователя список книг должен быть пустым")
        );
    }

    private boolean isValidUuid(String uuid) {
        if (uuid == null || uuid.isEmpty()) {
            return false;
        }
        try {
            UUID.fromString(uuid);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    @Step("Попытка регистрации (возвращает сырой Response для негативных тестов)")
    public Response tryRegistrationRaw(AccountModel credentials) {
        return client.registration(credentials);
    }

    @Step("Валидация ошибки регистрации с проверкой сообщения")
    public void verifyErrorMessage(Response response, String expectedCode, String expectedMessagePart, int status) {
        AccountErrorResponse error = response
                .then()
                .statusCode(status)
                .extract()
                .as(AccountErrorResponse.class);

        Assertions.assertAll("Валидация ошибки регистрации",
                () -> Assertions.assertEquals(expectedCode, error.getCode()),
                () -> Assertions.assertTrue(
                        error.getMessage().contains(expectedMessagePart),
                        "Сообщение об ошибке должно содержать: '" + expectedMessagePart +
                                "', но получено: '" + error.getMessage() + "'"
                )
        );
    }

    @Step("Попытка входа с невалидными данными (возвращает сырой Response)")
    public Response tryLoginRaw(AccountModel credentials) {
        return client.auth(credentials);
    }

    @Step("Валидация ошибки авторизации")
    public void verifyLoginError(Response response, String expectedCode) {
        AccountErrorResponse error = response
                .then()
                .statusCode(404)
                .extract()
                .as(AccountErrorResponse.class);

        Assertions.assertAll("Валидация ошибки Login",
                () -> Assertions.assertEquals(expectedCode, error.getCode(),
                        "Код ошибки не совпадает"),
                () -> Assertions.assertNotNull(error.getMessage(),
                        "Сообщение об ошибке пустое")
        );
    }

}
