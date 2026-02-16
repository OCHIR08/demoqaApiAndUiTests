package com.demoqa.base;

import com.demoqa.api.services.AccountService;
import com.demoqa.config.Config;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeAll;

public class BaseApiTest {
    public static String authToken;
    public static String userId;
    public static RequestSpecification requestSpec;

    @BeforeAll
    public static void setup() {
        // Настройка глобального логирования для Allure
        RestAssured.filters(new io.qameta.allure.restassured.AllureRestAssured());
        RestAssured.baseURI = Config.baseUrl();

        // Авторизация вынесена в сервис
        AccountService accountService = new AccountService();
        Response response = accountService.login(Config.loginIvan(), Config.passwordIvan())
                .then().statusCode(200).extract().response();

        authToken = response.path("token");
        userId = response.path("userId");

        // Сборка спецификации
        requestSpec = new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .addHeader("Authorization", "Bearer " + authToken)
                .build();
    }
}