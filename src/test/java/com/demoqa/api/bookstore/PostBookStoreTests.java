package com.demoqa.api.bookstore;

import com.demoqa.api.clients.BookClient;
import com.demoqa.api.models.request.AccountModel;
import com.demoqa.api.models.request.addBook.AddBooksModel;
import com.demoqa.api.models.response.GetTokenResponse;
import com.demoqa.api.models.response.addBook.AddBooksResponseModel;
import com.demoqa.api.models.response.registration.RegistrationResponse;
import com.demoqa.api.services.AccountServices;
import com.demoqa.api.services.BookServices;
import com.demoqa.api.spec.ProjectSpecs;
import io.qameta.allure.Owner;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static com.demoqa.factory.BookFactory.getFirstValidIsbn;
import static com.demoqa.factory.BookFactory.getNonExistentIsbn;
import static com.demoqa.factory.UserFactory.*;
import static com.demoqa.utils.ErrorMessages.*;
import static com.demoqa.utils.ErrorMessages.MSG_USER_ID_NOT_CORRECT;
import static io.restassured.RestAssured.given;

public class PostBookStoreTests {
    private final BookServices bookServices = new BookServices();
    private final AccountServices accountServices = new AccountServices();
    private final BookClient client = new BookClient();

    // ✅Positive-проверки
    @Test
    @Tag("api")
    @Tag("positive")
    @Tag("e2e")
    @Owner("Ermoshkaev")
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("POST /Books: добавить 1 книгу → 201")
    void addSingleBook_success() {
        // Arrange
        AccountModel user = createValidUser();
        String validIsbn = getFirstValidIsbn();

        RegistrationResponse reg = accountServices.registrationNew(user);
        GetTokenResponse tokenResp = accountServices.getToken(user);

        AddBooksModel body = AddBooksModel.ofSingle(reg.getUserId(), validIsbn);

        // Act & Assert
        AddBooksResponseModel response = bookServices.addBooksToUser(
                reg.getUserId(), tokenResp.getToken(), body);

        Assertions.assertNotNull(response.getBooks());
        Assertions.assertEquals(1, response.getBooks().size());
    }

    // ❌  Negative-проверки
    @Test
    @Tag("api")
    @Tag("negative")
    @Owner("Ermoshkaev")
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("POST /Books: невалидный ISBN → 400")
    void addBook_invalidIsbn_400() {
        // Arrange
        AccountModel user = createValidUser();
        RegistrationResponse reg = accountServices.registrationNew(user);
        GetTokenResponse tokenResp = accountServices.getToken(user);

        String nonExistentIsbn = getNonExistentIsbn();
        String token = tokenResp.getToken();
        String userId = reg.getUserId();

        AddBooksModel body = AddBooksModel.ofSingle(userId, nonExistentIsbn);
        // Act
        Response response = client.addBooks(userId, token, body);
        // Assert
        bookServices.verifyErrorMessage(response, CODE_1205, MSG_ISBN_NOT_AVAILABLE, 400);
    }


    @Test
    @Tag("api")
    @Tag("negative")
    @Owner("Ermoshkaev")
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("POST /Books: пустой список ISBN → 400")
    void addBook_emptyList_400() {
        // Arrange
        AccountModel user = createValidUser();
        RegistrationResponse reg = accountServices.registrationNew(user);
        GetTokenResponse tokenResp = accountServices.getToken(user);

        String userId = reg.getUserId();
        String token = tokenResp.getToken();

        AddBooksModel body = AddBooksModel.builder()
                .userId(userId)
                .collectionOfIsbns(Collections.emptyList())
                .build();

        // Act
        Response response = client.addBooks(userId, token, body);
        // Assert
        response.then().statusCode(400);
    }

    @Test
    @Tag("api")
    @Tag("negative")
    @Owner("Ermoshkaev")
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("POST /Books: без токена → 401")
    void addBook_noAuth_401() {
        // Arrange
        String validIsbn = getFirstValidIsbn();
        String userId = nonExistentUuid();

        AddBooksModel body = AddBooksModel.ofSingle(userId, validIsbn);
        // Act
        Response response = given()
                .spec(ProjectSpecs.requestSpec()) // ❌ без Authorization
                .body(body)
                .post("/BookStore/v1/Books");
        // Assert
        response.then().statusCode(401);
    }

    @Test
    @Tag("api")
    @Tag("negative")
    @Owner("Ermoshkaev")
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("POST /Books: неверный токен → 401")
    void addBook_invalidToken_401() {
        // Arrange
        String validIsbn = getFirstValidIsbn();
        String userId = nonExistentUuid();
        String invalidToken = nonValidToken();

        AddBooksModel body = AddBooksModel.ofSingle(userId, validIsbn);
        // Act
        Response response = given()
                .spec(ProjectSpecs.authorizedRequestSpec(invalidToken))
                .body(body)
                .post("/BookStore/v1/Books");
        // Assert
        response.then().statusCode(401);
    }

    @Test
    @Tag("api")
    @Tag("negative")
    @Owner("Ermoshkaev")
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("POST /Books: userId не существует → 401")
    void addBook_nonExistentUser_401() {
        // Arrange
        AccountModel user = createValidUser();
        accountServices.registrationNew(user);
        GetTokenResponse tokenResp = accountServices.getToken(user);

        String validIsbn = getFirstValidIsbn();
        String token = tokenResp.getToken();
        String nonExistentUserId = nonExistentUuid();

        AddBooksModel body = AddBooksModel.ofSingle(nonExistentUserId, validIsbn);
        // Act
        Response response = client.addBooks(nonExistentUserId, token, body);
        // Assert
        bookServices.verifyErrorMessage(response, CODE_1207, MSG_USER_ID_NOT_CORRECT, 401);
    }
}
