package com.demoqa.api.steps;

import com.demoqa.api.assertions.AccountAssertions;
import com.demoqa.api.services.AccountService;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class AccountSteps {
    private final String userId;
    private final RequestSpecification spec;

    public AccountSteps(String userId, RequestSpecification spec) {
        this.userId = userId;
        this.spec = spec;
    }

    @Step("Проверить, что профиль пользователя {expectedUsername} пуст")
    public void verifyProfileIsEmpty(String expectedUsername) {
        Response response = AccountService.getUserProfile(userId, spec);
        new AccountAssertions(response)
                .statusCodeIs200()
                .checkUsername(expectedUsername)
                .profileBooksListIsEmpty();
    }


}