package com.demoqa.api;


import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import com.demoqa.api.clients.LoginStepsApi;
import com.demoqa.config.Config;
import io.qameta.allure.Owner;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;


public class LoginTestApi {

    private final LoginStepsApi loginSteps = new LoginStepsApi();

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
        String username = Config.loginIvan();
        String password = Config.passwordIvan();

        loginSteps.login(username, password)
                .then()
                .log().status()
                .log().body()
                .statusCode(200)
                .body("username", is(username))
                .body("token", notNullValue());
    }
}

