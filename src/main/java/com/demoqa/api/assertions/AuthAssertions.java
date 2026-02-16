package com.demoqa.api.assertions;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.*;

public class AuthAssertions {
    private final Response response;

    public AuthAssertions(Response response) {
        this.response = response;
    }

    @Step("Проверить успешность авторизации (200 OK, Schema, Username)")
    public void isSuccessfullyLoggedIn(String expectedUsername) {
        response.then()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("login-schema.json"))
                .body("username", is(expectedUsername))
                .header("Content-Type", is("application/json; charset=utf-8"))
                .time(lessThan(3000L));
    }

}
