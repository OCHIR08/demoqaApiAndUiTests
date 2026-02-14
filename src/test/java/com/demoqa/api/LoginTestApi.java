package com.demoqa.api;


import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import com.demoqa.api.clients.LoginStepsApi;
import com.demoqa.base.BaseApiTest;
import com.demoqa.config.Config;
import io.qameta.allure.Owner;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

public class LoginTestApi extends BaseApiTest {

    private final LoginStepsApi loginSteps = new LoginStepsApi();
    String username = Config.loginIvan();
    String password = Config.passwordIvan();

    @Test
    @Tag("api")
    @Owner("ermoshkaev")
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("Проверка успешной авторизации")
    void successfulLoginTest() {
        loginSteps.login(username, password)
                .then()
                .log().status()
                .log().body()
                .statusCode(200)
                .body("username", is(username))
                .body("token", notNullValue());
    }

    @Test
    @Tag("api")
    @Owner("ermoshkaev")
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("Запрос списка книг")
    void getBooks() {
        given()
                .header("Authorization", "Bearer " + authToken)
                .contentType(ContentType.JSON)
                .when()
                .get("/BookStore/v1/Books")
                .then()
                .log().all();
    }

    @Test
    @Tag("api")
    @Owner("ermoshkaev")
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("Добавленик книг к клиенту")
    void addBookToUser() {

        given()
                .header("Authorization", "Bearer " + authToken)
                .contentType(ContentType.JSON)
                .body("{\n" +
                        "  \"userId\":" + userId +
                        "  \"collectionOfIsbns\": [\n" +
                        "    {\n" +
                        "      \"isbn\": \"9781449331818\"\n" +
                        "    }\n" +
                        "  ]\n" +
                        "}")

                .when()
                .post("/BookStore/v1/Books")
                .then()
                .log().all() // Выведет лог ответа в консоль для отладки
                .log().status()
                .log().body()
                .statusCode(201);
    }

    @Test
    @Tag("api")
    @Owner("ermoshkaev")
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("Проверить наличие книг у клиента")
    void checkBookClient(){
        given()
                .header("Authorization", "Bearer " + authToken)
                .contentType(ContentType.JSON)
                .when()
                .get("/Account/v1/User/"+userId)
                .then()
                .log().all();
    }

    @Test
    @Tag("api")
    @Owner("ermoshkaev")
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("Удаление книги у клиента")
    void deletBookClient(){
        given()
                .header("Authorization", "Bearer" + authToken)
                .contentType(ContentType.JSON)
                .body("{\n" +
                        "  \"isbn\": \"9781449331818\",\n" +
                        "  \"userId\":" + userId +
                        "}")
                .when()
                .delete("/BookStore/v1/Book")
                .then()
                .log().all();
    }


}

