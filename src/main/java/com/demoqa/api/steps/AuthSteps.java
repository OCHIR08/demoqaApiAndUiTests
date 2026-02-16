package com.demoqa.api.steps;

import com.demoqa.api.assertions.AuthAssertions;
import com.demoqa.api.services.AccountService;
import io.qameta.allure.Step;

public class AuthSteps {
    private final AccountService loginApi = new AccountService();

    @Step("Выполнить вход в систему и проверить результат")
    public void loginAndVerify(String username, String password) {
        AuthAssertions assertions = new AuthAssertions(loginApi.login(username, password));
        assertions.isSuccessfullyLoggedIn(username);
    }
}