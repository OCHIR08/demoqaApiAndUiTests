package com.demoqa.api;

import static com.demoqa.api.services.BookService.getUserProfile;

import com.demoqa.api.assertions.AccountAssertions;
import com.demoqa.api.assertions.BookAssertions;
import com.demoqa.api.services.AccountService;
import com.demoqa.api.services.BookService;
import com.demoqa.api.steps.AuthSteps;
import com.demoqa.base.BaseApiTest;
import com.demoqa.config.Config;
import io.qameta.allure.Owner;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

public class ApiTest extends BaseApiTest {

    private final AuthSteps loginSteps = new AuthSteps();
    private final String username = Config.loginIvan();
    private final String password = Config.passwordIvan();

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
    @DisplayName("Запрос списка книг")
    void getBooks() {
        BookService.getAllBook(requestSpec);
        io.restassured.response.Response profile = getUserProfile(requestSpec, userId);
        new BookAssertions(profile).checkTypeDataRequest("books-schema.json");
    }

    @Test
    @Tag("api")
    @Owner("ermoshkaev")
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("E2E: Полный цикл управления книгой в профиле")
    void bookLifecycleTest() {
        // 1. ПОДГОТОВКА: Удаляем все книги (чтобы тест был независимым)
        BookService.deleteAllBooks(userId, requestSpec);
        // 2. ДЕЙСТВИЕ: Добавляем книгу через нашу модель
        BookService.addBook("9781449331818", userId, requestSpec);
        // 3. ПРОВЕРКА: Запрашиваем профиль пользователя и сверяем данные
        io.restassured.response.Response profile = getUserProfile(requestSpec, userId);
        new BookAssertions(profile).checkBookInProfile(username, "9781449331818");
    }

    @Test
    @Tag("api")
    @Owner("ermoshkaev")
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("Удаление книги у клиента")
    void deleteBookClient(){
        BookService.deleteAllBooks(userId, requestSpec);
        // ПОДГОТОВКА: 1. ДЕЙСТВИЕ: даляем все книги (чтобы тест был независимым)/Добавляем книгу через нашу модель
        BookService.deleteAllBooks(userId, requestSpec);
        BookService.addBook("9781449331818", userId, requestSpec);
        // 2.  Удаляем  книгу
        BookService.deleteAllBooks(userId, requestSpec);
        // 3. ПРОВЕРКА: Убеждаемся, что в профиле пусто
        Response profile = AccountService.getUserProfile(userId, requestSpec);
        // Красивый Fluent Assert
        new AccountAssertions(profile)
                .statusCodeIs200()
                .checkUsername(username)
                .profileBooksListIsEmpty();
    }

}

