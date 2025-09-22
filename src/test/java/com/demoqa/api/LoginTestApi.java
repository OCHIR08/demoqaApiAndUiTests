package com.demoqa.api;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.hamcrest.Matchers.is;

import com.demoqa.api.models.LoginRequest;
import com.demoqa.config.Config;
import io.qameta.allure.Owner;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

public class LoginTestApi  {

    @BeforeAll
    public static void setup() {
        RestAssured.baseURI = Config.baseUrl();
    }

    @Test
    @Tag("api")
    @Owner("ermoshkaev")
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("Проверка успешной авторизации")
    void successfulLoginTest() {
        LoginRequest requestBody = new LoginRequest();
        requestBody.setUserName(Config.loginIvan());
        requestBody.setPassword(Config.passwordIvan());

        given()
                .body(requestBody)
                .contentType(JSON)
                .log().uri()

                .when()
                .post("/Account/v1/Login")

                .then()
                .log().status()
                .log().body()

                .statusCode(200)
                .body("username", is(Config.loginIvan()))
                .body("password", is(Config.passwordIvan()));
    }
}

