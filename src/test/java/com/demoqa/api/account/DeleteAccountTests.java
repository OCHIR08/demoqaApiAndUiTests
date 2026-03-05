package com.demoqa.api.account;

import com.demoqa.api.models.request.AccountModel;
import com.demoqa.api.models.response.GetTokenResponse;
import com.demoqa.api.models.response.registration.RegistrationResponse;
import com.demoqa.api.models.testdata.AuthorizedUser;
import com.demoqa.api.services.AccountServices;
import com.demoqa.factory.UserFactory;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static com.demoqa.factory.UserFactory.createValidUser;
import static com.demoqa.utils.ErrorMessages.*;

public class DeleteAccountTests {
    private final AccountServices accountServices = new AccountServices();

    //✅ Positive-проверки
    @Test
    @Tag("api")
    @Tag("positive")
    @DisplayName("DELETE User: успешное удаление → 204")
    void deleteUser_returns204() {
        // Arrange
        AccountModel user = createValidUser();
        // Act
        RegistrationResponse response = accountServices.registrationNew(user);
        String token = accountServices.getToken(user).getToken();
        String userId = response.getUserId();
        // Assert
        accountServices.deleteUserAndVerify(userId, token);
    }

    @Test
    @Tag("api")
    @Tag("positive")
    @DisplayName("DELETE User: после удаления пользователь недоступен → 404")
    void deleteUser_makesUserInaccessible() {
        // Arrange
        AccountModel user = createValidUser();
        // Act
        RegistrationResponse response = accountServices.registrationNew(user);
        String token = accountServices.getToken(user).getToken();
        String userId = response.getUserId();
        //Assert
        accountServices.deleteUser(userId, token)
                .then().statusCode(204);
        accountServices.verifyUserDeleted(userId, token);
    }

    @Test
    @Tag("api") @Tag("positive") @Tag("e2e") @Severity(SeverityLevel.BLOCKER)
    @DisplayName("E2E: Register → Login → Get → Delete → Verify")
    void e2e_userLifecycle_fullFlow() {
        // Arrange: создаём пользователя
        AccountModel validUser = UserFactory.createValidUser();

        AuthorizedUser user = accountServices.registerNewUser(validUser);

        // Act + Assert: полный жизненный цикл
        accountServices.verifyUserInfo(user.userId(), user.token(), user.credentials().getUserName());  // Get
        accountServices.deleteUserE2E(user.userId(), user.token());                                      // Delete
        accountServices.verifyUserInaccessible(user.userId(), user.token());                             // Verify deletion
        accountServices.verifyLoginFailsAfterDeletion(user.credentials());                               // Verify login fails
    }

    //    ❌ Negative-проверки
    @Test
    @Tag("api")
    @Tag("negative")
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("DELETE User: без токена → 401")
    void deleteUser_noAuth_error401() {
        //Act
        Response response = accountServices.deleteWithoutAuth(UserFactory.validUserIdIvan());
        //Assert
        accountServices.verifyErrorMessage(response,CODE_1200,MSG_USER_NOT_AUTH,401);
    }

    //    Баг verifyErrorMessage приходит 200 OK при ошибке, нарушает REST-принципы
    @Test
    @Tag("api")
    @Tag("negative")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("DELETE User: несуществующий UserId → 404")
    void deleteUser_nonExistent_error404() {
        // Arrange
        AccountModel user = createValidUser();
        //Act
        GetTokenResponse tokenResp = accountServices.getToken(user);
        String token = tokenResp.getToken();
        String userId = UserFactory.nonExistentUuid();
        Response response = accountServices.deleteUser(userId, token);
        //Assert
        accountServices.verifyErrorMessage(response, CODE_1207, MSG_USER_ID_NOT_CORRECT, 404);
    }

//    Баг verifyErrorMessage приходит 200 OK при ошибке, нарушает REST-принципы
    @Test
    @Tag("api")
    @Tag("negative")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("DELETE User: повторное удаление → 404 (idempotent)")
    void deleteUser_idempotent() {
        // Arrange
        AccountModel user = createValidUser();
        //Act
        RegistrationResponse reg = accountServices.registrationNew(user);
        GetTokenResponse tokenResp = accountServices.getToken(user);
        String userId = reg.getUserId();
        String token = tokenResp.getToken();
        accountServices.deleteUser(userId, token); // Первое удаление
        Response response2 = accountServices.deleteUser(userId, token); // Повторное удаление того же userId
        //Assert
        accountServices.verifyErrorMessage(response2, CODE_1207, MSG_USER_NOT_FOUND, 404);
    }
}
