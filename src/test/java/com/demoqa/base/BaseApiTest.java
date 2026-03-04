package com.demoqa.base;

import com.demoqa.api.models.request.AccountModel;
import com.demoqa.api.models.response.GetTokenResponse;
import com.demoqa.api.models.response.LoginResponse;
import com.demoqa.api.services.AccountServices;
import com.demoqa.config.Config;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

public class BaseApiTest {

    protected String authToken;
    protected String userId;

    private final AccountServices accountServices = new AccountServices();

    @BeforeAll
    public static void setUp() {
    }

    @BeforeEach
    public void setupTest(){
        this.authToken = null;
        this.userId = null;
    }

    protected void authenticate(String username, String password) {
        AccountModel credentials = AccountModel.builder()
                .userName(username)
                .password(password)
                .build();

        // 🔥 Генерируем свежий токен
        GetTokenResponse tokenResponse = accountServices.getToken(credentials);

        if (!"Success".equals(tokenResponse.getStatus()) || tokenResponse.getToken() == null) {
            throw new IllegalStateException(
                    "Failed to generate token: " + tokenResponse.getResult());
        }

        this.authToken = tokenResponse.getToken();

        // 🔥 Получаем userId через логин (или кэшируем при регистрации)
        LoginResponse loginResponse = accountServices.login(credentials);
        this.userId = loginResponse.getUserId();
    }

    protected void authenticateWithConfig() {
        authenticate(Config.loginIvan(), Config.passwordIvan());
    }

    protected String getAuthToken() {
        if (authToken == null) {
            authenticateWithConfig();
        }
        return authToken;
    }

    protected String getUserId() {
        if (userId == null) {
            authenticateWithConfig();
        }
        return userId;
    }
}