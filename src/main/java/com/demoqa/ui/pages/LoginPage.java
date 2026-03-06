package com.demoqa.ui.pages;

import com.codeborne.selenide.SelenideElement;
import com.demoqa.api.models.request.AccountModel;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;

public class LoginPage {
    private final SelenideElement fieldLogin = $("#userName");
    private final SelenideElement fieldPassword = $("#password");
    private final SelenideElement loginBtn = $("#login");
    private final SelenideElement userNameValue = $("#userName-value");


    public void openLogin() {
        open("/login");
    }

    public void login(AccountModel user) {
        $(fieldLogin).setValue(user.getUserName());
        $(fieldPassword).setValue(user.getPassword());
        $(loginBtn).click();
    }

    public void verifyUserNameValue(String name) {
        $(userNameValue).shouldHave(text(name));

    }
}
