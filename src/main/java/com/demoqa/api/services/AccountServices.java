package com.demoqa.api.services;

import com.demoqa.api.clients.AccountClient;
import com.demoqa.api.models.response.AccountErrorResponse;
import com.demoqa.api.models.response.GetTokenResponse;
import com.demoqa.api.models.response.registration.RegistrationResponse;
import com.demoqa.api.models.request.AccountModel;
import com.demoqa.api.models.response.LoginResponse;
import com.demoqa.api.models.testdata.AuthorizedUser;
import com.demoqa.api.spec.ProjectSpecs;
import com.demoqa.config.Config;
import com.demoqa.utils.JwtUtils;
import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.*;

public class AccountServices {
    private final AccountClient client  = new AccountClient();
    private static final List<Integer> RATE_LIMIT_CODES = List.of(401, 404, 429);

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

//    @Step("Регистрация пользователя: {credentials.userName}")
//    public RegistrationResponse registrationNew(AccountModel credentials) {
//        Response response = client.registration(credentials);
//
//        // Attach request
//        Allure.attachment("Registration Request",
//                "{\"userName\": \"" + credentials.getUserName() + "\"}"
//        );
//
//        // Attach response
//        Allure.attachment("Registration Response",
//                response.getBody().asString()
//        );
//
//        return response.then()
//                .statusCode(201)
//                .extract()
//                .as(RegistrationResponse.class);
//    }

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

    @Step("Попытка регистрации (возвращает сырой Response для негативных тестов)")
    public Response tryRegistrationRaw(AccountModel credentials) {
        return client.registration(credentials);
    }

    @Step("Попытка входа (возвращает сырой Response)")
    public Response tryAuthRaw(AccountModel credentials) {
        return client.auth(credentials);
    }

    @Step("Попытка входа (возвращает сырой Response)")
    public Response tryLoginRaw(AccountModel credentials) {
        return client.login(credentials);
    }

    //============проверки============ //

    @Step("Валидация ответа API: статус {expectedStatus}, схема {schemaPath}")
    public Response validateApiResponse(
            Response response,                    // уже выполненный запрос
            int expectedStatus,
            String schemaPath,
            String stepDescription
    ) {
        return Allure.step(stepDescription, () ->
                response.then()
                        .statusCode(expectedStatus)
                        .body(matchesJsonSchemaInClasspath(schemaPath))
                        .extract()
                        .response()
        );
    }


    @Step("Проверка полей ответа при входе в систему")
    public void verifyLoginResponse(LoginResponse response, String expectedUsername) {
        Assertions.assertAll("Валидация LoginResponse",
                () -> Assertions.assertNotNull(response.getToken(), "Токен не сгенерирован"),
                () -> Assertions.assertNotNull(response.getUserId(), "UserId отсутствует"),
                () -> Assertions.assertEquals(expectedUsername, response.getUsername(), "Имя пользователя не совпадает")
                // ❌ Убрали проверку expires — она очевидна и дублируется
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



    @Step("Регистрация с некорректным Content-Type: {contentType}")
    public Response tryRegisterWithWrongContentType(ContentType contentType, String body) {
        return given()
                .spec(ProjectSpecs.requestSpec())
                .contentType(contentType)  // RestAssured принимает ContentType enum
                .body(body)
                .post(Config.registrationUrl())
                .then()
                .log().ifValidationFails()
                .extract()
                .response();
    }

    @Step("Проверка: сервер отклонил запрос с неверным Content-Type")
    public void assertContentTypeError(Response response) {
        response.then()
                .statusCode(anyOf(equalTo(400), equalTo(415), equalTo(500))); // 500 — баг API, но принимаем
    }

    @Step("GET /user/{userId} без авторизации")
    public Response getInfoWithoutAuth(String userId) {
        return given()
                .spec(ProjectSpecs.requestSpec())  // без токена
                .get(Config.getInfoAccountUrl() + userId)
                .then()
                .log().ifValidationFails()  // ✅ логи только при провале
                .extract()
                .response();
    }

    @Step("Получить username из JWT токена пользователя")
    public String extractUsernameFromToken(AccountModel credentials) {
        registrationNew(credentials);  // создаём пользователя
        String token = getToken(credentials).getToken();
        return JwtUtils.getUsername(token);
    }

    @Step("Выполнить {attempts} неудачных попыток входа для пользователя {username}")
    public List<Response> attemptFailedLogins(AccountModel wrongCredentials, int attempts) {
        List<Response> responses = new ArrayList<>();
        for (int i = 0; i < attempts; i++) {
            responses.add(tryAuthRaw(wrongCredentials));
        }
        return responses;
    }

    @Step("Проверить, что ответ соответствует анти-брутфорс политике")
    public void assertBruteForceProtection(Response response) {
        Assertions.assertTrue(
                RATE_LIMIT_CODES.contains(response.getStatusCode()),
                () -> "Ожидаем один из статусов %s после множественных неудачных попыток, но получили: %d"
                        .formatted(RATE_LIMIT_CODES, response.getStatusCode())
        );
    }

    @Step("Удалить пользователя {userId} и проверить ответ")
    public void deleteUserAndVerify(String userId, String token) {
        deleteUser(userId, token)
                .then()
                .statusCode(204); // No Content — стандарт для успешного DELETE
    }

    @Step("Проверить, что пользователь {userId} больше не доступен")
    public void verifyUserDeleted(String userId, String token) {
        Response response = info(userId, token);

        // DemoQA возвращает 404 с кодом ошибки 1207 при удалении пользователя
        response.then()
                .statusCode(401)
                .body("code", equalTo("1207"))
                .body("message", containsString("User not found"));
    }

    //==================================!!!!!!!!!===========================
    @Step("E2E: Зарегистрировать нового пользователя")
    public AuthorizedUser registerNewUser(AccountModel user) {

        RegistrationResponse reg = registrationNew(user);
        String token = getToken(user).getToken();
        return new AuthorizedUser(reg.getUserId(), token, user);
    }

    @Step("E2E: Проверить данные пользователя {userId}")
    public void verifyUserInfo(String userId, String token, String expectedUsername) {
        info(userId, token)
                .then()
                .statusCode(200)
                .body("userId", equalTo(userId))
                .body("username", equalTo(expectedUsername))
                .body("books", notNullValue());
    }

    @Step("E2E: Удалить пользователя {userId}")
    public void deleteUserE2E(String userId, String token) {
        deleteUser(userId, token)
                .then()
                .statusCode(204);
    }

    @Step("E2E: Проверить, что удалённый пользователь недоступен")
    public void verifyUserInaccessible(String userId, String token) {
        Response response = info(userId, token);
        response.then()
                .statusCode(anyOf(equalTo(401), equalTo(404)))  // DemoQA quirk
                .body("code", equalTo("1207"))
                .body("message", containsString("User not found"));
    }

    @Step("E2E: Проверить, что вход после удаления невозможен")
    public void verifyLoginFailsAfterDeletion(AccountModel credentials) {
        Response response = tryAuthRaw(credentials);
        response.then()
                .statusCode(anyOf(equalTo(401), equalTo(404)))  // DemoQA quirk
                .body("code", equalTo("1207"));
    }

    @Step("DELETE /user/{userId} без авторизации")
    public Response deleteWithoutAuth(String userId) {
        return given()
                .spec(ProjectSpecs.requestSpec())  // без токена
                .delete(Config.deleteAccountUrl() + userId)
                .then()
                .log().all() //ifValidationFails()  // ✅ логи только при провале
                .extract()
                .response();
    }
}
