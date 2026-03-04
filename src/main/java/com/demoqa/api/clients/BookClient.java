package com.demoqa.api.clients;

import com.demoqa.api.models.request.DeleteBookModel;
import com.demoqa.api.models.request.ReplaceBookModel;
import com.demoqa.api.models.request.addBook.AddBooksModel;
import com.demoqa.api.spec.ProjectSpecs;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class BookClient {

    // Получить весь список книг
    public Response getAllBooks() {
        return given()
                .spec(ProjectSpecs.requestSpec())
                .get("/BookStore/v1/Books")
                .then()
                .log().all()
                .extract()
                .response();
    }

    // Получить описание книги по ISBN
    public Response getBookByIsbn(String isbn) {
        return given()
                .spec(ProjectSpecs.requestSpec())
                .queryParam("ISBN", isbn)
                .get("/BookStore/v1/Book")
                .then()
                .log().all()
                .extract()
                .response();
    }

    // Добавить книгу клиенту
    public Response addBooks(String userId, String token, AddBooksModel body) {
        return given()
                .spec(ProjectSpecs.authorizedRequestSpec(token))
                .body(body)
                .post("/BookStore/v1/Books")
                .then()
                .log().all()
                .extract()
                .response();
    }

    // Замена книги у клиента
    public Response replaceBooks(String isbn, String token, ReplaceBookModel body) {
        return given()
                .spec(ProjectSpecs.authorizedRequestSpec(token))
                .body(body)
                .log().all()
                .put("/BookStore/v1/Books/"+isbn)
                .then()
                .log().all()
                .extract()
                .response();
    }

    // Удалить конкретную книги у клиента по userId и isbn
    public Response deleteBook(DeleteBookModel body, String token) {
        return given()
                .spec(ProjectSpecs.authorizedRequestSpec(token))
                .body(body)
                .delete("/BookStore/v1/Book")  // 🔥 Единственное число!
                .then()
                .log().all()
                .extract()
                .response();
    }

    // Удалить все книги у клиента по userId
    public Response deleteAllBooks(String userId, String token) {
        return given()
                .spec(ProjectSpecs.authorizedRequestSpec(token))
                .queryParam("UserId", userId)
                .delete("/BookStore/v1/Books") // 🔥 Множественное число!
                .then()
                .log().all()
                .extract()
                .response();
    }


    /**
     * GET /BookStore/v1/Books?UserId={userId}
     * Возвращает коллекцию конкретного пользователя (требует авторизации)
     */
    public Response getUserBooks(String userId, String token) {
        return given()
                .spec(ProjectSpecs.requestSpec())
                .header("Authorization", "Bearer " + token)
                .accept("application/json")
                .queryParam("UserId", userId)  // 🔥 Ключевое отличие: добавляем UserId
                .log().ifValidationFails()
                .when()
                .get("/BookStore/v1/Books")    // 🔥 Тот же endpoint, но с query-параметром
                .then()
                .log().ifValidationFails()
                .extract()
                .response();
    }

}