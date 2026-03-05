package com.demoqa.api.bookstore;

import com.demoqa.api.clients.BookClient;
import com.demoqa.api.models.request.AccountModel;
import com.demoqa.api.models.request.ReplaceBookModel;
import com.demoqa.api.models.request.addBook.AddBooksModel;
import com.demoqa.api.models.response.GetTokenResponse;
import com.demoqa.api.models.response.book.GetBooksResponse;
import com.demoqa.api.models.response.registration.RegistrationResponse;
import com.demoqa.api.services.AccountServices;
import com.demoqa.api.services.BookServices;
import io.qameta.allure.Owner;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static com.demoqa.factory.BookFactory.*;
import static com.demoqa.factory.BookFactory.getFirstValidIsbn;
import static com.demoqa.factory.UserFactory.createValidUser;
import static com.demoqa.utils.ErrorMessages.*;
import static com.demoqa.utils.ErrorMessages.MSG_ISBN_NOT_AVAILABLE;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.equalTo;

public class PutBookStoreTests {
    private final BookServices bookServices = new BookServices();
    private final AccountServices accountServices = new AccountServices();
    private final BookClient client = new BookClient();

    //✅ Positive-проверки
    @Test
    @Tag("api")
    @Tag("positive")
    @Owner("Ermoshkaev")
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("PUT /Books/{ISBN}: заменить книгу → 200/201")
    void replaceBook_success() {
        // Arrange: создать пользователя + добавить книгу
        AccountModel user = createValidUser();
        RegistrationResponse reg = accountServices.registrationNew(user);
        GetTokenResponse tokenResp = accountServices.getToken(user);

        String oldIsbn = getFirstValidIsbn();
        String newIsbn = getSecondValidIsbn();

        // Добавить старую книгу
        AddBooksModel addBody = AddBooksModel.ofSingle(reg.getUserId(), oldIsbn);
        bookServices.addBooksToUser(reg.getUserId(), tokenResp.getToken(), addBody);

        // Act: заменить
        ReplaceBookModel replaceBody = ReplaceBookModel.builder()
                .userId(reg.getUserId())
                .isbn(newIsbn)
                .build();

        Response response = bookServices.replaceUserBooks(oldIsbn, tokenResp.getToken(), replaceBody);

        // Assert
        response.then().statusCode(anyOf(equalTo(200), equalTo(201)));

        // Проверить, что в коллекции теперь newIsbn
        GetBooksResponse books = bookServices.getUserBooksCollection(reg.getUserId(), tokenResp.getToken());
        Assertions.assertTrue(books.getBooks().stream()
                .anyMatch(b -> b.getIsbn().equals(newIsbn)));
    }

    // ❌  Negative-проверки
    @Test
    @Tag("api")
    @Tag("negative")
    @Owner("Ermoshkaev")
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("PUT /Books/{ISBN}: ISBN в path не существует → 400")
    void replaceBook_nonExistentIsbn_400() {
        // Arrange
        AccountModel user = createValidUser();
        RegistrationResponse reg = accountServices.registrationNew(user);
        GetTokenResponse tokenResp = accountServices.getToken(user);

        String nonExistentIsbn = getNonExistentIsbn();
        String validIsbn = getFirstValidIsbn();

        ReplaceBookModel body = ReplaceBookModel.builder()
                .userId(reg.getUserId())
                .isbn(validIsbn)
                .build();
        // Act
        Response response = client.replaceBooks(nonExistentIsbn, tokenResp.getToken(), body);
        // Assert
        bookServices.verifyErrorMessage(response, CODE_1206, MSG_ISBN_NOT_AVAILABLE_IN_USER_COLLECTION, 400);
    }

    @Test
    @Tag("api")
    @Tag("negative")
    @Owner("Ermoshkaev")
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("PUT /Books/{ISBN}: новый ISBN не валиден → 400")
    void replaceBook_invalidNewIsbn_400() {
        // Arrange
        AccountModel user = createValidUser();
        String nonExistentIsbn = getNonExistentIsbn();
        String validIsbn = getFirstValidIsbn();

        RegistrationResponse reg = accountServices.registrationNew(user);
        GetTokenResponse tokenResp = accountServices.getToken(user);

        ReplaceBookModel body = ReplaceBookModel.builder()
                .userId(reg.getUserId())
                .isbn(nonExistentIsbn)
                .build();
        // Act
        Response response = client.replaceBooks(validIsbn, tokenResp.getToken(), body);
        // Assert
        bookServices.verifyErrorMessage(response, CODE_1205, MSG_ISBN_NOT_AVAILABLE, 400);
    }
}
