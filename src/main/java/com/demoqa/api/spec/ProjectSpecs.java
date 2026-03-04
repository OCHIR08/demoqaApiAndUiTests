package com.demoqa.api.spec;

import com.demoqa.config.Config;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

public class ProjectSpecs {

//    public static RequestSpecification requestSpec() {
//        return new RequestSpecBuilder()
//                .setBaseUri(Config.apiBaseUrl())
//                .setContentType(ContentType.JSON)
//                .addFilter(new AllureRestAssured()) // Логирование для отчетов
//                .build();
//    }
public static RequestSpecification requestSpec() {

    String baseUrl = Config.apiBaseUrl();
    if (baseUrl == null) {
        throw new IllegalStateException("apiBaseUrl not configured.  " +
                "Ensure 'https://demoqa.com' is defined in config.properties");
    }

    RequestSpecification requestSpec = new RequestSpecBuilder()
            .setBaseUri(baseUrl)
            .setContentType(ContentType.JSON)
            .addFilter(new AllureRestAssured())
            .build();
    return requestSpec;
}

    public static RequestSpecification authorizedRequestSpec(String token) {
        return new RequestSpecBuilder()
                .setBaseUri(Config.apiBaseUrl())
                .setContentType(ContentType.JSON)
                .addHeader("Authorization", "Bearer " + token)
                .addFilter(new AllureRestAssured())
                .build();
    }

    public static ResponseSpecification responseSpec(int status) {
        return new ResponseSpecBuilder()
                .expectStatusCode(status)
                .build();
    }


}