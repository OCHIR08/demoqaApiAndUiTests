package com.demoqa.ui;

import com.codeborne.selenide.Configuration;
import com.demoqa.config.Config;
import com.demoqa.ui.steps.LoginSteps;
import org.junit.jupiter.api.*;

import static com.codeborne.selenide.Selenide.*;

public class LoginTestUI {

    @BeforeEach
    void setUp() {
        Configuration.baseUrl = Config.baseUrl();
        Configuration.browser = "chrome";
        Configuration.browserSize = "1920x1080";
        Configuration.pageLoadTimeout = 60000; // 60 секунд
    }

    @Test
    @Tag("ui")
//    @Owner("ermoshkaev")
//    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("Проверка успешной авторизации")
    public void successLogin(){
        new LoginSteps()
                .openProfile()
                .login(Config.loginIvan(),Config.passwordIvan())
                .verifyUsernameDisplay(Config.loginIvan());
    }

    @AfterEach
    void tearDown() {
        closeWebDriver();
    }
}
