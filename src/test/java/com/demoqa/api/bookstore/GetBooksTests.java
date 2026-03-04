package com.demoqa.api.bookstore;

import com.demoqa.api.clients.BookClient;
import com.demoqa.api.models.request.AccountModel;
import com.demoqa.api.models.request.DeleteBookModel;
import com.demoqa.api.models.request.ReplaceBookModel;
import com.demoqa.api.models.request.addBook.AddBooksModel;
import com.demoqa.api.models.response.GetTokenResponse;
import com.demoqa.api.models.response.addBook.AddBooksResponseModel;
import com.demoqa.api.models.response.book.GetBookResponse;
import com.demoqa.api.models.response.book.GetBooksResponse;
import com.demoqa.api.models.response.registration.RegistrationResponse;
import com.demoqa.api.services.AccountServices;
import com.demoqa.api.services.BookServices;
import com.demoqa.api.spec.ProjectSpecs;
import io.qameta.allure.Description;
import io.qameta.allure.Owner;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static com.demoqa.factory.UserFactory.createValidUser;
import static com.demoqa.utils.ErrorMessages.*;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class GetBooksTests{
    private final BookServices bookServices = new BookServices();
    private final AccountServices accountServices = new AccountServices();
    private final BookClient client = new BookClient();


    // GET /BookStore/v1/Books — Получить все книги (публичный)
    // ✅Positive-проверки
    @Test
    @Tag("api")
    @Tag("positive")
    @Owner("Ermoshkaev")
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("Валидация JSON схемы на типы данных и обязательные поля")
    @Description("Проверка: схемы и типов ответа, обязателных полей, статус 200")
    void getAllBocks(){
        // Assert
        bookServices.validateSchemaAllBook();
    }

    @Test
    @Tag("api")
    @Tag("positive")
    @Owner("Ermoshkaev")
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("GET /Books: response содержит массив books")
    void getAllBooks_hasBooksArray() {
        // Act
        GetBooksResponse response = bookServices.getAllBooks();
        // Assert
        Assertions.assertNotNull(response.getBooks());
        Assertions.assertFalse(response.getBooks().isEmpty());
    }

    @Test
    @Tag("api")
    @Tag("positive")
    @Owner("Ermoshkaev")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("GET /Books: каждая книга имеет обязательные поля")
    void getAllBooks_requiredFields() {
        // Act
        GetBooksResponse response = bookServices.getAllBooks();
        // Assert
        response.getBooks().forEach(book -> {
            Assertions.assertAll("Поля книги",
                    () -> Assertions.assertNotNull(book.getIsbn()),
                    () -> Assertions.assertNotNull(book.getTitle()),
                    () -> Assertions.assertNotNull(book.getAuthor())
            );
        });
    }

    // ❌  Negative-проверки
    @Test
    @Tag("api")
    @Tag("negative")
    @Owner("Ermoshkaev")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("GET /Books: Content-Type = application/json")
    void getAllBooks_contentType() {
        // Act
        Response response = client.getAllBooks();
        // Assert
        response.then().contentType(ContentType.JSON);
    }

    @Test
    @Tag("api")
    @Tag("performance")
    @Owner("Ermoshkaev")
    @Severity(SeverityLevel.MINOR)
    @DisplayName("GET /Books: время ответа < 2 сек")
    void getAllBooks_responseTime() {
        // Act
        Response response = client.getAllBooks();
        // Assert
        response.then().time(lessThan(2000L));
    }

    //   GET /BookStore/v1/Book?ISBN={isbn} — Получить книгу по ISBN
    // ✅Positive-проверки
    @Test
    @Tag("api")
    @Tag("positive")
    @Owner("Ermoshkaev")
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("GET /Book: валидный ISBN → 200 + данные книги")
    void getBookByValidIsbn() {
        String validIsbn = "9781449325862";
        // Act
        GetBookResponse book = bookServices.getBookByIsbn(validIsbn);
        // Assert
        Assertions.assertEquals(validIsbn, book.getIsbn());
        Assertions.assertNotNull(book.getTitle());
    }
    // ❌  Negative-проверки
    @Test
    @Tag("api")
    @Tag("negative")
    @Owner("Ermoshkaev")
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("GET /Book: несуществующий ISBN → 400 + error message")
    @Description("Проверка: кода ответа,сообщение об ошибке , статус 400")
    void getBook_invalidIsbn_400(){
        // Act
        Response response = bookServices.tryGetNonExistentBook("2134");
        // Assert
        bookServices.verifyErrorMessage(response, CODE_1205,MSG_ISBN_NOT_AVAILABLE,400);
    }

    @Test
    @Tag("api")
    @Tag("negative")
    @Owner("Ermoshkaev")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("GET /Book: пустой ISBN → 400")
    void getBook_emptyIsbn_400() {
        // Act
        Response response = client.getBookByIsbn("");
        // Assert
        bookServices.verifyErrorMessage(response, CODE_1205, MSG_ISBN_NOT_AVAILABLE, 400);
    }

    @Test
    @Tag("api")
    @Tag("negative")
    @Owner("Ermoshkaev")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("GET /Book: ISBN с спецсимволами → 400")
    void getBook_isbnWithSpecialChars_400() {
        // Act
        Response response = client.getBookByIsbn("978-1-4493-<script>");
        // Assert
        bookServices.verifyErrorMessage(response, CODE_1205, MSG_ISBN_NOT_AVAILABLE, 400);
    }

    // PUT /BookStore/v1/Books/{ISBN} — Заменить книгу (требует auth)
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

        String oldIsbn = "9781449325862";
        String newIsbn = "9781593277574";

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

        ReplaceBookModel body = ReplaceBookModel.builder()
                .userId(reg.getUserId())
                .isbn("9781593277574")
                .build();
        // Act
        Response response = client.replaceBooks("NON_EXISTENT_ISBN", tokenResp.getToken(), body);
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
        RegistrationResponse reg = accountServices.registrationNew(user);
        GetTokenResponse tokenResp = accountServices.getToken(user);
        ReplaceBookModel body = ReplaceBookModel.builder()
                .userId(reg.getUserId())
                .isbn("INVALID_NEW_ISBN")
                .build();
        // Act
        Response response = client.replaceBooks("9781449325862", tokenResp.getToken(), body);
        // Assert
        bookServices.verifyErrorMessage(response, CODE_1205, MSG_ISBN_NOT_AVAILABLE, 400);
    }

    //    POST /BookStore/v1/Books — Добавить книги пользователю (требует auth)
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
    RegistrationResponse reg = accountServices.registrationNew(user);
    GetTokenResponse tokenResp = accountServices.getToken(user);

    AddBooksModel body = AddBooksModel.ofSingle(reg.getUserId(), "9781449325862");

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

        AddBooksModel body = AddBooksModel.ofSingle(reg.getUserId(), "INVALID_ISBN");
        // Act
        Response response = client.addBooks(reg.getUserId(), tokenResp.getToken(), body);
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

        AddBooksModel body = AddBooksModel.builder()
                .userId(reg.getUserId())
                .collectionOfIsbns(Collections.emptyList())
                .build();
        // Act
        Response response = client.addBooks(reg.getUserId(), tokenResp.getToken(), body);
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
        AddBooksModel body = AddBooksModel.ofSingle("any-user-id", "9781449325862");
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
        AddBooksModel body = AddBooksModel.ofSingle("any-user-id", "9781449325862");
        // Act
        Response response = given()
                .spec(ProjectSpecs.authorizedRequestSpec("invalid.token.here"))
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

        AddBooksModel body = AddBooksModel.ofSingle("non-existent-uuid", "9781449325862");
        // Act
        Response response = client.addBooks("non-existent-uuid", tokenResp.getToken(), body);
        // Assert
        bookServices.verifyErrorMessage(response, CODE_1207, MSG_USER_ID_NOT_CORRECT, 401);
    }

//DELETE /BookStore/v1/Book — Удалить одну книгу (требует auth)
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

    String isbnToDelete = "9781449325862";
    AddBooksModel addBody = AddBooksModel.ofSingle(reg.getUserId(), isbnToDelete);
    bookServices.addBooksToUser(reg.getUserId(), tokenResp.getToken(), addBody);

    // Act
    DeleteBookModel deleteBody = DeleteBookModel.builder()
            .userId(reg.getUserId())
            .isbn(isbnToDelete)
            .build();

    Response response = bookServices.deleteBookFromUser(deleteBody, tokenResp.getToken());

    // Assert
    response.then().statusCode(204);

    // Проверить, что книга удалена
    GetBooksResponse books = bookServices.getUserBooksCollection(reg.getUserId(), tokenResp.getToken());
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
        RegistrationResponse reg = accountServices.registrationNew(user);
        GetTokenResponse tokenResp = accountServices.getToken(user);

        DeleteBookModel body = DeleteBookModel.builder()
                .userId(reg.getUserId())
                .isbn("NON_EXISTENT_ISBN")
                .build();
        // Act
        Response response = client.deleteBook(body, tokenResp.getToken());
        // Assert
        response.then().statusCode(400);
    }
}
