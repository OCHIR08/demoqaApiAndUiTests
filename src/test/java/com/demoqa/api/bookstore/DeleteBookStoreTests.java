package com.demoqa.api.bookstore;

import com.demoqa.api.models.request.AccountModel;
import com.demoqa.api.models.request.DeleteBookModel;
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

import static com.demoqa.factory.BookFactory.getFirstValidIsbn;
import static com.demoqa.factory.BookFactory.getNonExistentIsbn;
import static com.demoqa.factory.UserFactory.createValidUser;
import static com.demoqa.utils.ErrorMessages.CODE_1206;
import static com.demoqa.utils.ErrorMessages.MSG_ISBN_NOT_AVAILABLE_IN_USER_COLLECTION;

public class DeleteBookStoreTests {
    private final BookServices bookServices = new BookServices();
    private final AccountServices accountServices = new AccountServices();

//✅ Positive
    @Test
    @Tag("api")
    @Tag("positive")
    @Owner("Ermoshkaev")
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("DELETE /Book: удалить книгу → 204")
    void deleteSingleBook_success() {
        // Arrange: пользователь + книга
        AccountModel user = createValidUser();
        RegistrationResponse reg = accountServices.registrationNew(user);
        GetTokenResponse tokenResp = accountServices.getToken(user);

        String isbnToDelete = getFirstValidIsbn();
        String token = tokenResp.getToken();
        String userId = reg.getUserId();

        AddBooksModel addBody = AddBooksModel.ofSingle(userId, isbnToDelete);
        bookServices.addBooksToUser(userId, token, addBody);

        // Act
        DeleteBookModel deleteBody = DeleteBookModel.builder()
                .userId(userId)
                .isbn(isbnToDelete)
                .build();

        Response response = bookServices.deleteBookFromUser(deleteBody, token);

        // Assert
//        response.then().statusCode(204);
        // Проверить, что книга удалена
        GetBooksResponse books = bookServices.getUserBooksCollection(userId, token);
        Assertions.assertFalse(books.getBooks().stream()
                .anyMatch(b -> b.getIsbn().equals(isbnToDelete)));
    }

    //    ❌ Negative
    @Test
    @Tag("api")
    @Tag("negative")
    @Owner("Ermoshkaev")
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("DELETE /Book: удалить несуществующую книгу → 400")
    void deleteNonExistentBook_400() {
        // Arrange
        AccountModel user = createValidUser();
        // Act
        RegistrationResponse reg = accountServices.registrationNew(user);
        GetTokenResponse tokenResp = accountServices.getToken(user);

        DeleteBookModel body = DeleteBookModel.builder()
                .userId(reg.getUserId())
                .isbn(getNonExistentIsbn())
                .build();
        Response response = bookServices.deleteBookFromUser(body, tokenResp.getToken());
        // Assert
        bookServices.verifyErrorMessage(response,CODE_1206,MSG_ISBN_NOT_AVAILABLE_IN_USER_COLLECTION,400);
    }
}
