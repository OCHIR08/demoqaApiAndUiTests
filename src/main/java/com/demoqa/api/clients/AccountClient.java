package com.demoqa.api.clients;

import com.demoqa.api.models.request.AccountModel;
import com.demoqa.api.spec.ProjectSpecs;
import com.demoqa.config.Config;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class AccountClient {

    public Response login(AccountModel credentials) {
        return given()
                .spec(ProjectSpecs.requestSpec()) // Наш шаблон из предыдущего шага
                .body(credentials)
                .post(Config.loginUri())
                .then()
                .log().all()
                .extract()
                .response();
    }

    public Response registration(AccountModel credentials){
        return given()
                .spec(ProjectSpecs.requestSpec())
                .body(credentials)
                .post(Config.registrationUrl())
                .then()
                .log().all()
                .extract()
                .response();
    }

    public Response getInfoAccount(String userId, String token){
        return given()
                .spec(ProjectSpecs.authorizedRequestSpec(token))
                .get(Config.getInfoAccountUrl()+userId)
                .then()
                .log().all()
                .extract()
                .response();

    }

    public Response deleteAccount(String userId,String token){
        return given()
                .spec(ProjectSpecs.authorizedRequestSpec(token))
                .delete(Config.deleteAccountUrl()+userId)
                .then()
                .log().all()
                .extract()
                .response();
    }

    public Response generateToken(AccountModel credentials) {
        return given()
                .spec(ProjectSpecs.requestSpec())
                .body(credentials)
                .post(Config.generateTokenUrl())
                .then()
                .log().all()
                .extract()
                .response();
    }

    public Response auth(AccountModel credentials){
        return given()
                .spec(ProjectSpecs.requestSpec())
                .body(credentials)
                .post(Config.authorizedUrl())
                .then()
                .log().all()
                .extract()
                .response();
    }
}
