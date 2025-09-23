package com.demoqa.api.clients;


import com.demoqa.api.models.LoginRequest;
import io.qameta.allure.Step;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;

public class LoginStepsApi {

    @Step("Логинимся пользователем {username}")
    public Response login(String username, String password) {
        LoginRequest request = new LoginRequest();
        request.setUserName(username);
        request.setPassword(password);

        return given()
                .body(request)
                .contentType(JSON)
                .log().uri()
                .when()
                .post("/Account/v1/Login");
    }
}
