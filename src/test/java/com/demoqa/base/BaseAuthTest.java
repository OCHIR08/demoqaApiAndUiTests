package com.demoqa.base;


import com.demoqa.api.spec.ProjectSpecs;
import io.restassured.RestAssured;
import io.restassured.filter.log.LogDetail;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeEach;

public class BaseAuthTest extends BaseApiTest {
    protected RequestSpecification authSpec;

    @BeforeEach
    public void setupAuth() {
        // 🔥 Гарантируем свежий токен перед каждым тестом
        if (getAuthToken() == null) {
            authenticateWithConfig();
        }

        this.authSpec = RestAssured.given()
                .spec(ProjectSpecs.requestSpec())
                .header("Authorization", "Bearer " + getAuthToken())
                .log().ifValidationFails(LogDetail.ALL);

    }
}
