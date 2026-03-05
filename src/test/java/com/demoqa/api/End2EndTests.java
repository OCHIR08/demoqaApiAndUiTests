package com.demoqa.api;

import com.demoqa.api.models.request.AccountModel;
import com.demoqa.api.models.request.DeleteBookModel;
import com.demoqa.api.models.request.addBook.AddBooksModel;
import com.demoqa.api.models.response.GetTokenResponse;
import com.demoqa.api.models.response.book.GetBooksResponse;
import com.demoqa.api.models.response.registration.RegistrationResponse;
import com.demoqa.api.services.AccountServices;
import com.demoqa.api.services.BookServices;
import com.demoqa.factory.BookFactory;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.demoqa.factory.UserFactory.createValidUser;

public class End2EndTests {
    private final AccountServices accountServices = new AccountServices();
    private final BookServices bookServices = new BookServices();

    @Test
    @Tag("api") @Tag("e2e") @Severity(SeverityLevel.BLOCKER)
    @DisplayName("E2E: Полный цикл пользователя с книгами")
     void e2e_userWithBooks_fullLifecycle() {
        // 1. Регистрация
        AccountModel user = createValidUser();
        RegistrationResponse reg = accountServices.registrationNew(user);

        // 2. Получение токена
        GetTokenResponse tokenResp = accountServices.getToken(user);
        String userId = reg.getUserId();
        String token = tokenResp.getToken();

        // 3. Добавить 2 книги
        List<String> isbns = BookFactory.getValidIsbns(2);
        AddBooksModel addBody = AddBooksModel.of(userId, isbns);
        bookServices.addBooksToUser(userId, token, addBody);

        // 4. Проверить коллекцию
        GetBooksResponse books = bookServices.getUserBooksCollection(userId, token);
//        Assertions.assertEquals(2, books.getBooks().size());

        // 5. Удалить 1 книгу
        DeleteBookModel deleteBody = DeleteBookModel.builder()
                .userId(userId).isbn(isbns.get(0)).build();
        bookServices.deleteBookFromUser(deleteBody, token)
                .then().statusCode(204);

        // 6. Проверить: осталась 1 книга
        GetBooksResponse afterDelete = bookServices.getUserBooksCollection(userId, token);
        Assertions.assertEquals(1, afterDelete.getBooks().size());

        // 7. Удалить пользователя
        accountServices.deleteUser(userId, token).then().statusCode(204);

        // 8. Проверить: пользователь и его данные удалены
        Response info = accountServices.info(userId, token);
        info.then().statusCode(404);
    }
}
