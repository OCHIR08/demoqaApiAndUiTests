package com.demoqa.ui;


import com.demoqa.api.models.request.AccountModel;
import com.demoqa.base.BaseUiTest;
import com.demoqa.config.Config;
import com.demoqa.ui.steps.LoginStepsUi;
import io.qameta.allure.Owner;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.junit.jupiter.api.*;

import static com.demoqa.factory.UserFactory.createValidUser;

public class LoginUiTest extends BaseUiTest {

    LoginStepsUi loginStepsUi = new LoginStepsUi();

    @Test
    @Tag("ui")
    @Owner("ermoshkaev")
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("Проверка успешной авторизации")
    public void successLogin(){
        loginStepsUi.openProfile();
        AccountModel user = createValidUser();
        loginStepsUi.login(user);
        loginStepsUi.verifyUsernameDisplay(Config.loginIvan());
    }
}
