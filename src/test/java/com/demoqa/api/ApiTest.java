package com.demoqa.api;

import com.demoqa.api.steps.AccountSteps;
import com.demoqa.api.steps.AuthSteps;
import com.demoqa.api.steps.BookSteps;
import com.demoqa.base.BaseApiTest;
import com.demoqa.config.Config;
import io.qameta.allure.Owner;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

public class ApiTest extends BaseApiTest {

    private final AuthSteps loginSteps = new AuthSteps();
    private final String username = Config.loginIvan();
    private final String password = Config.passwordIvan();
    private final BookSteps bookSteps = new BookSteps(userId, requestSpec);
    private final AccountSteps accountSteps = new AccountSteps(userId, requestSpec);
    @Test
    @Tag("api")
    @Owner("ermoshkaev")
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("Проверка успешной авторизации")
    void successfulLoginTest() {
        loginSteps.loginAndVerify(username, password);
    }

    @Test
    @Tag("api")
    @Owner("ermoshkaev")
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("Валидация JSON схемы на типы данных и обязательные поля")
    void getBooks() {
        bookSteps.veryfiJsonShema("books-schema.json");
    }

    @Test
    @Tag("api")
    @Owner("ermoshkaev")
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("E2E: Полный цикл управления книгой в профиле")
    void bookLifecycleTest() {
        bookSteps.clearProfile();
        bookSteps.addBookToProfile("9781449331818");
        bookSteps.verifyBookInProfile(username, "9781449331818");
    }

    @Test
    @Tag("api")
    @Owner("ermoshkaev")
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("Удаление книги у клиента")
    void deleteBookClient(){
        bookSteps.clearProfile();
        bookSteps.addBookToProfile("9781449331818");
        bookSteps.clearProfile();
        accountSteps.verifyProfileIsEmpty(username);
    }

}

