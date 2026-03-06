package com.demoqa.api.services;

import com.demoqa.api.clients.BookClient;
import com.demoqa.api.models.request.DeleteBookModel;
import com.demoqa.api.models.request.ReplaceBookModel;
import com.demoqa.api.models.request.addBook.AddBooksModel;
import com.demoqa.api.models.response.BookErrorResponse;
import com.demoqa.api.models.response.addBook.AddBooksResponseModel;
import com.demoqa.api.models.response.book.GetBookResponse;
import com.demoqa.api.models.response.book.GetBooksResponse;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

public class BookServices {
    private final BookClient client = new BookClient();

    @Step("Получить все книги (публично)")
    public GetBooksResponse getAllBooks() {
        return client.getAllBooks()
                .then()
                .statusCode(200)
                .extract()
                .as(GetBooksResponse.class);
    }

    @Step("Проверка схемы getAllBooks")
    public void validateSchemaAllBook() {
        Response response = client.getAllBooks();
        response.then()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("all-books-schema.json"));
    }


    @Step("Получить книгу по ISBN (публично)")
    public GetBookResponse getBookByIsbn(String isbn) {
        return client.getBookByIsbn(isbn)
                .then()
                .statusCode(200)
                .extract()
                .as(GetBookResponse.class);
    }

    @Step("Попытка получить несуществующую книгу (возвращает сырой Response)")
    public Response tryGetNonExistentBook(String isbn) {
        return client.getBookByIsbn(isbn);
    }

    @Step("Валидация ошибки регистрации с проверкой сообщения")
    public void verifyErrorMessage(Response response, String expectedCode, String expectedMessagePart, int status) {
        BookErrorResponse error = response
                .then()
                .statusCode(status)
                .extract()
                .as(BookErrorResponse.class);

        Assertions.assertAll("Валидация ошибки регистрации",
                () -> Assertions.assertEquals(expectedCode, error.getCode()),
                () -> Assertions.assertTrue(
                        error.getMessage().contains(expectedMessagePart),
                        "Сообщение об ошибке должно содержать: '" + expectedMessagePart +
                                "', но получено: '" + error.getMessage() + "'"
                )
        );
    }

    @Step("Добавить книги в коллекцию пользователя")
    public AddBooksResponseModel addBooksToUser(String userId, String token, AddBooksModel body) {
        return client.addBooks(userId,token,body)
                .then()
                .statusCode(201)
                .extract()
                .as(AddBooksResponseModel.class);
    }

    @Step("Удалить одну книгу из коллекции")
    public Response deleteBookFromUser(DeleteBookModel body, String token) {
        return client.deleteBook(body, token);
    }

    @Step("Удалить все книги пользователя")
    public Response deleteAllUserBooks(String userId, String token) {
        return client.deleteAllBooks(userId, token);
    }

    @Step("Полностью заменить коллекцию книг пользователя")
    public Response replaceUserBooks(String isbn, String token, ReplaceBookModel body) {
        return client.replaceBooks(isbn, token, body);
    }

    /**
     * Получить книги пользователя через BookStore API
     * GET /BookStore/v1/Books?UserId={userId}
     */
    @Step("Получить книги пользователя: UserId={0}")
    public GetBooksResponse getUserBooksCollection(String userId, String token) {
        return client.getUserBooks(userId, token)
                .then()
                .log().all()
                .statusCode(200)
                .contentType("application/json")  // 🔥 Проверяем, что пришёл JSON
                .extract()
                .as(GetBooksResponse.class);
    }


}