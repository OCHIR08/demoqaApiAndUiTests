package com.demoqa.api.bookstore;

import com.demoqa.api.clients.BookClient;
import com.demoqa.api.models.response.book.GetBookResponse;
import com.demoqa.api.models.response.book.GetBooksResponse;
import com.demoqa.api.services.BookServices;
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

import static com.demoqa.factory.BookFactory.*;
import static com.demoqa.utils.ErrorMessages.*;
import static org.hamcrest.Matchers.*;

public class GetBooksTests{
    private final BookServices bookServices = new BookServices();
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
        String validIsbn = getFirstValidIsbn();
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
        // Arrange
        String nonExistentIsbn = getNonExistentIsbn();
        // Act
        Response response = bookServices.tryGetNonExistentBook(nonExistentIsbn);
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
        // Arrange
        String emptyIsbn = getEmptyIsbn();
        // Act
        Response response = client.getBookByIsbn(emptyIsbn);
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
        // Arrange
        String invalidFormatIsbn = getInvalidFormatIsbn();
        // Act
        Response response = client.getBookByIsbn(invalidFormatIsbn);
        // Assert
        bookServices.verifyErrorMessage(response, CODE_1205, MSG_ISBN_NOT_AVAILABLE, 400);
    }
}
