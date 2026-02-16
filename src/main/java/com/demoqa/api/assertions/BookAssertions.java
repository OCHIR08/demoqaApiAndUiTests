package com.demoqa.api.assertions;

import io.qameta.allure.Step;
import io.restassured.response.Response;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.*;

public class BookAssertions {
    private final Response response;

    public BookAssertions(Response response) {
        this.response = response;
    }

    @Step("Проверить, что книга {expectedIsbn} присутствует в профиле пользователя {username}")
    public void checkBookInProfile(String username, String expectedIsbn) {
        response.then()
                .statusCode(200)
                .body("username", is(username))
                .body("books[0].isbn", is(expectedIsbn))
                .body("books", hasSize(1));
    }

    @Step("Проверка типов данных и обязательных полей ")
    public void checkTypeDataRequest(String path){
        response.then()
                .body(matchesJsonSchemaInClasspath(path))
                .statusCode(200)
                .time(lessThan(1500L));
    }
}