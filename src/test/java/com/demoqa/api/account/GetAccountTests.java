package com.demoqa.api.account;

import com.demoqa.api.models.request.AccountModel;
import com.demoqa.api.models.request.addBook.AddBooksModel;
import com.demoqa.api.models.response.GetTokenResponse;
import com.demoqa.api.models.response.registration.RegistrationResponse;
import com.demoqa.api.services.AccountServices;
import com.demoqa.api.services.BookServices;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static com.demoqa.factory.BookFactory.getFirstValidIsbn;
import static com.demoqa.factory.UserFactory.createValidUser;
import static com.demoqa.factory.UserFactory.nonValidToken;
import static com.demoqa.utils.ErrorMessages.*;
import static org.hamcrest.Matchers.*;

public class GetAccountTests {
    private final AccountServices accountServices = new AccountServices();
    private final BookServices bookServices = new BookServices();

    //  ✅ Positive-проверки
    @Test
    @Tag("api")
    @Tag("positive")
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("GET User: авторизованный запрос → 200 + полные данные")
        void getUser_authenticated_success() {
        // Arrange
        AccountModel user = createValidUser();
        RegistrationResponse reg = accountServices.registrationNew(user);
        GetTokenResponse tokenResp = accountServices.getToken(user);

        String userId = reg.getUserId();
        String token = tokenResp.getToken();
        // Act
        Response response = accountServices.info(userId, token);
        // Assert
            response.then()
                .statusCode(200)
                .body("userId", equalTo(userId))
                .body("username", equalTo(user.getUserName()))
                .body("books", notNullValue());
}

    @Test
    @Tag("api")
    @Tag("positive")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("GET User: books — массив объектов с isbn")
    void getUser_booksArray_structure() {
        // Arrange
        // Сначала добавляем книгу через BookStore API
        AccountModel user = createValidUser();
        RegistrationResponse reg = accountServices.registrationNew(user);
        GetTokenResponse tokenResp = accountServices.getToken(user);

        String validIsbn = getFirstValidIsbn();
        String token = tokenResp.getToken();
        String userId = reg.getUserId();
        // Act
        AddBooksModel addBody = AddBooksModel.ofSingle(userId, validIsbn);
        bookServices.addBooksToUser(userId, token, addBody);
        Response response = accountServices.info(userId, token);
        // Assert
        response.then()
                .body("books[0].isbn", equalTo(validIsbn))
                .body("books[0].title", notNullValue())
                .body("books[0].author", notNullValue());
    }

    @Test
    @Tag("api")
    @Tag("positive")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("GET User: валидация JSON Schema")
    void getUser_validateSchema() {
        // Arrange
        AccountModel user = createValidUser();
        RegistrationResponse reg = accountServices.registrationNew(user);
        GetTokenResponse tokenResp = accountServices.getToken(user);

        String userId = reg.getUserId();
        String token = tokenResp.getToken();
        // Act
        Response response = accountServices.info(userId, token);
        // Assert
        accountServices.validateApiResponse(
                response,
                200,
                "account-user-schema.json",
                "Проверка схемы ответа логина" );
    }

    //    ❌ Negative-проверки
    @Test
    @Tag("api")
    @Tag("negative")
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("GET User: без токена → 401")
    void getUser_noAuth_error401() {
        // Arrange
        String fakeUserId = UUID.randomUUID().toString();
        // Act
        Response response = accountServices.getInfoWithoutAuth(fakeUserId);
        // Assert
        accountServices.verifyErrorMessage(response, CODE_1200, MSG_USER_NOT_AUTH, 401);
}

    @Test
    @Tag("api")
    @Tag("negative")
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("GET User: неверный токен → 401")
    void getUser_invalidToken_error401() {
        // Arrange
        String fakeUserId = UUID.randomUUID().toString();
        String invalidToken = nonValidToken();
        // Act
        Response response = accountServices.info(fakeUserId,invalidToken);
        // Assert
        accountServices.verifyErrorMessage(response,CODE_1207, MSG_USER_NOT_FOUND, 401);
    }

    @Test
    @Tag("api")
    @Tag("negative")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("GET User: несуществующий UserId → 404 + code 1207")
    void getUser_nonExistentUserId_error404() {
        // Arrange
        AccountModel user = createValidUser();
        GetTokenResponse tokenResp = accountServices.getToken(user);
        String token = tokenResp.getToken();
        // Act
        Response response = accountServices.info("non-existent-uuid", token);
        // Assert
        accountServices.verifyErrorMessage(response, CODE_1207, MSG_USER_NOT_FOUND, 401);
    }

    @Test
    @Tag("api")
    @Tag("negative")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("GET User: UserId не UUID формат → 401")
    void getUser_invalidUserIdFormat_error401() {
        // Arrange
        AccountModel user = createValidUser();
        GetTokenResponse tokenResp = accountServices.getToken(user);
        String token = tokenResp.getToken();
        // Act
        Response response = accountServices.info("not-a-uuid", token);
        // Assert
        accountServices.verifyErrorMessage(response, CODE_1207, MSG_USER_NOT_FOUND, 401);
    }

}
