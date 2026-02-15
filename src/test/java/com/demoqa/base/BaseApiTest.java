package com.demoqa.base;

import com.demoqa.api.clients.LoginStepsApi;
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
        RestAssured.baseURI = Config.baseUrl();



        final LoginStepsApi loginSteps = new LoginStepsApi();
        String username = Config.loginIvan();
        String password = Config.passwordIvan();

        Response response = loginSteps.login(username, password)
                .then()
                .statusCode(200)
                .extract()
                .response();

        authToken = response.path("token");
        userId = response.path("userId");

        requestSpec = new RequestSpecBuilder()
                .setBaseUri(Config.baseUrl())
                .setContentType(ContentType.JSON)
                .addHeader("Authorization", "Bearer " + authToken)
                .build();

    }
}
