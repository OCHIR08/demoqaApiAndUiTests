package com.demoqa.ui.steps;

import com.demoqa.ui.pages.LoginPage;
import io.qameta.allure.Step;

public class LoginStepsUi {
    private final LoginPage loginPage = new LoginPage();

    @Step("Открыть страницу /loggin")
    public void openProfile() {
        loginPage.openLogin();
    }

    @Step("Авторизация")
    public void login(String login, String password) {
        loginPage.login(login,password);
    }

    @Step("Убедитесь, что пользователь вошел в систему")
    public void verifyUsernameDisplay(String name) {
        loginPage.verifyUserNameValue(name);
    }
}
