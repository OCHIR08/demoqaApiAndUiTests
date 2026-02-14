package com.demoqa.ui;


import com.demoqa.base.BaseUiTest;
import com.demoqa.config.Config;
import com.demoqa.ui.steps.LoginStepsUi;
import io.qameta.allure.Owner;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.junit.jupiter.api.*;

public class LoginTestUI extends BaseUiTest {

    LoginStepsUi loginStepsUi = new LoginStepsUi();

    @Test
    @Tag("ui")
    @Owner("ermoshkaev")
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("Проверка успешной авторизации")
    public void successLogin(){
        loginStepsUi.openProfile();
        loginStepsUi.login(Config.loginIvan(),Config.passwordIvan());
        loginStepsUi.verifyUsernameDisplay(Config.loginIvan());
    }
}
