package com.demoqa.api.services;

import com.demoqa.api.models.AddBookModel;
import com.demoqa.api.models.IsbnModel;
import io.qameta.allure.Step;
import io.restassured.specification.RequestSpecification;
import io.restassured.response.Response;

import java.util.List;

import static io.restassured.RestAssured.given;

public class BookService {
    @Step("Добавить книгу с ISBN {isbn} пользователю {userId}")
    public static void addBook(String isbn, String userId, RequestSpecification requestSpec) {
    AddBookModel requestBody = AddBookModel.builder()
            .userId(userId)
            .collectionOfIsbns(List.of(new IsbnModel(isbn)))
            .build();

    given()
            .spec(requestSpec)
            .body(requestBody)
            .when()
            .post("/BookStore/v1/Books")
            .then()
            .statusCode(201);
}
    @Step("Удалить все книги пользователя {userId}")
    public static void deleteAllBooks(String userId, RequestSpecification requestSpec) {
    given()
            .spec(requestSpec)
            .queryParam("UserId", userId)
            .when()
            .delete("/BookStore/v1/Books")
            .then()
            .statusCode(204);
}

    @Step("Получить информацию о пользователе {userId}")
    public static Response getUserProfile(RequestSpecification requestSpec, String userId) { // Поменяли порядок!
        return given()
                .spec(requestSpec)
                .when()
                .get("/Account/v1/User/" + userId)
                .then()
                .extract().response(); // Экстрактим Response для ассертов;
    }

    @Step("Получить информацию о всех книгах")
    public static void getAllBook(RequestSpecification requestSpec) {
        given()
                .spec(requestSpec)
                .when()
                .get("/BookStore/v1/Books");
    }
}
