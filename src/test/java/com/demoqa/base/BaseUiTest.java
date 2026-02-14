package com.demoqa.base;

import com.codeborne.selenide.Configuration;
import com.demoqa.config.Config;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import static com.codeborne.selenide.Selenide.closeWebDriver;

public class BaseUiTest {
    @BeforeEach
    void setUp() {
        Configuration.baseUrl = Config.baseUrl();
        Configuration.browser = "chrome";
        Configuration.browserSize = "1920x1080";
        Configuration.pageLoadTimeout = 60000; // 60 секунд
    }

    @AfterEach
    void tearDown() {
        closeWebDriver();
    }
}
