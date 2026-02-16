package com.demoqa.api.assertions;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

public class AccountAssertions {
    private final Response response;

    public AccountAssertions(Response response) {
        this.response = response;
    }

    @Step("Проверить, что статус код ответа 200")
    public AccountAssertions statusCodeIs200() {
        response.then().statusCode(200);
        return this;
    }

    @Step("Проверить, что список книг в профиле пуст")
    public AccountAssertions profileBooksListIsEmpty() {
        response.then().body("books", hasSize(0));
        return this;
    }

    @Step("Проверить имя пользователя")
    public AccountAssertions checkUsername(String expectedUsername) {
        response.then().body("username", is(expectedUsername));
        return this;
    }
}