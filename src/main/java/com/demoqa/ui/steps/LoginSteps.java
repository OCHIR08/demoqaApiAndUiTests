package com.demoqa.ui.steps;

import com.demoqa.ui.pages.LoginPage;
import io.qameta.allure.Step;

public class LoginSteps {
    private final LoginPage loginPage = new LoginPage();

    @Step("Открыть страницу /loggin")
    public LoginSteps openProfile() {
        loginPage.openLogin();
        return this;
    }

    @Step("Авторизация")
    public LoginSteps login(String login, String password) {
        loginPage.login(login,password);
        return this;
    }

    @Step("Убедитесь, что пользователь вошел в систему")
    public LoginSteps verifyUsernameDisplay(String name) {
        loginPage.verifyUserNameValue(name);
        return this;
    }
}
