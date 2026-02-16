package com.demoqa.api.steps;

import com.demoqa.api.assertions.BookAssertions;
import com.demoqa.api.services.BookService;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import static com.demoqa.api.services.BookService.getUserProfile;

public class BookSteps {
    private final String userId;
    private final RequestSpecification spec;

    // Конструктор принимает всё необходимое
    public BookSteps(String userId, RequestSpecification spec) {
        this.userId = userId;
        this.spec = spec;
    }

    @Step("Очистить профиль от всех книг")
    public void clearProfile() {
        BookService.deleteAllBooks(userId, spec);
    }

    @Step("Добавить книгу {isbn} в профиль")
    public void addBookToProfile(String isbn) {
        BookService.addBook(isbn, userId, spec);
    }

    @Step("Запрашиваем профиль пользователя и сверяем данные")
    public void verifyBookInProfile(String username, String number) {
        Response profile = getUserProfile(spec, userId);
        new BookAssertions(profile)
                .checkBookInProfile(username,number);
    }

    @Step("Валидация JSON схемы на типы данных и обязательные поля")
    public void veryfiJsonShema(String path) {
        Response  profile = getUserProfile(spec, userId);
        new BookAssertions(profile).checkTypeDataRequest(path);

    }
}