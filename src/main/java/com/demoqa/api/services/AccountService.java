package com.demoqa.api.services;


import com.demoqa.api.models.LoginRequest;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;

public class AccountService {
    @Step("Отправка запроса на логин для пользователя {username}")
    public Response login(String username, String password) {
        LoginRequest request = new LoginRequest();
        request.setUserName(username);
        request.setPassword(password);

        return given()
                .body(request)
                .contentType(JSON)
                .when()
                .post("/Account/v1/Login");
    }

    @Step("Получить информацию о профиле пользователя {userId}")
    public static Response getUserProfile(String userId, RequestSpecification spec) {
        return given()
                .spec(spec)
                .when()
                .get("/Account/v1/User/" + userId)
                .then()
                .extract().response();
    }

}
