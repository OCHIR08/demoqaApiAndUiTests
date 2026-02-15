package com.demoqa.api;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.*;

import com.demoqa.api.clients.LoginStepsApi;
import com.demoqa.api.models.AddBookModel;
import com.demoqa.api.models.DeleteBookModel;
import com.demoqa.api.models.IsbnModel;
import com.demoqa.base.BaseApiTest;
import com.demoqa.config.Config;
import io.qameta.allure.Owner;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;

public class LoginApiTest extends BaseApiTest {

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
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("login-schema.json"))
                .body("username", is(username))
                .header("Content-Type", is("application/json; charset=utf-8"))
                .time(lessThan(3000L));
    }

    @Test
    @Tag("api")
    @Owner("ermoshkaev")
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("Запрос списка книг")
    void getBooks() {
        given()
                .spec(requestSpec)
                .when()
                .get("/BookStore/v1/Books")
                .then()
                .body(matchesJsonSchemaInClasspath("books-schema.json"))
                .statusCode(200)
                .time(lessThan(1500L))
                .log().ifValidationFails();
    }

    @Test
    @Tag("api")
    @Owner("ermoshkaev")
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("E2E: Полный цикл управления книгой в профиле")
    void bookLifecycleTest() {
        // 1. ПОДГОТОВКА: Удаляем все книги (чтобы тест был независимым)
        given()
                .spec(requestSpec)
                .queryParam("UserId", userId)
                .when()
                .delete("/BookStore/v1/Books")
                .then()
                .statusCode(204);

        // 2. ДЕЙСТВИЕ: Добавляем книгу через нашу модель
        AddBookModel requestBody = AddBookModel.builder()
                .userId(userId)
                .collectionOfIsbns(List.of(new IsbnModel("9781449331818")))
                .build();

        given()
                .spec(requestSpec)
                .body(requestBody)
                .when()
                .post("/BookStore/v1/Books")
                .then()
                .statusCode(201);

        // 3. ПРОВЕРКА: Запрашиваем профиль пользователя и сверяем данные
        given()
                .spec(requestSpec)
                .when()
                .get("/Account/v1/User/" + userId)
                .then()
                .statusCode(200)
                .body("username", is(username))
                .body("books[0].isbn", is("9781449331818")) // Книга на месте!
                .body("books", hasSize(1)); // И она там ровно одна
    }


    @Test
    @Tag("api")
    @Owner("ermoshkaev")
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("Удаление книги у клиента")
    void deleteBookClient(){
        // ПОДГОТОВКА: 1. ДЕЙСТВИЕ: Добавляем книгу через нашу модель
        AddBookModel requestBody = AddBookModel.builder()
                .userId(userId)
                .collectionOfIsbns(List.of(new IsbnModel("9781449331818")))
                .build();

        given()
                .spec(requestSpec)
                .body(requestBody)
                .when()
                .post("/BookStore/v1/Books")
                .then();

        // 2.  Удаляем  книгу
        DeleteBookModel requestBodyDelete = DeleteBookModel.builder()
                .userId(userId)
                .isbn("9781449331818")
                .build();

        given()
                .spec(requestSpec)
                .body(requestBodyDelete)
                .when()
                .delete("/BookStore/v1/Book")
                .then()
                .statusCode(204)
                .log().all();

        // 3. ПРОВЕРКА: Убеждаемся, что в профиле пусто
        given()
                .header("Authorization", "Bearer " + authToken)
                .when()
                .get("/Account/v1/User/" + userId)
                .then()
                .statusCode(200)
                .body("books", hasSize(0));
    }
}

