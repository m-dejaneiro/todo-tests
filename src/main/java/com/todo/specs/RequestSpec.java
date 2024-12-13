package com.todo.specs;

import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.authentication.BasicAuthScheme;
import io.restassured.authentication.PreemptiveBasicAuthScheme;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

import java.util.List;

public class RequestSpec {
    private RequestSpecBuilder requestSpecBuilder;

    private static RequestSpecBuilder baseSpecBuilder() {
        RequestSpecBuilder requestSpecBuilder = new RequestSpecBuilder();
        requestSpecBuilder.addFilters(List.of(
                new RequestLoggingFilter(), new
                ResponseLoggingFilter(),
                new AllureRestAssured()));
        requestSpecBuilder.setContentType(ContentType.JSON);
        requestSpecBuilder.setAccept(ContentType.JSON);
        return requestSpecBuilder;
    }

    @Step("User - Not authorized.")
    public static RequestSpecification unauthSpec() {
        return baseSpecBuilder().build();
    }

    @Step("User - Admin. ")
    public static RequestSpecification authSpec() {
        PreemptiveBasicAuthScheme basicAuthScheme = new PreemptiveBasicAuthScheme();
        RequestSpecBuilder authBaseSpecBuilder = baseSpecBuilder();

        basicAuthScheme.setUserName("admin");
        basicAuthScheme.setPassword("admin");

        authBaseSpecBuilder.setAuth(basicAuthScheme);

        return authBaseSpecBuilder.build();
    }

    @Step("User - Invalid creds.")
    public static RequestSpecification invalidCredsSpec() {
        PreemptiveBasicAuthScheme basicAuthScheme = new PreemptiveBasicAuthScheme();
        RequestSpecBuilder authBaseSpecBuilder = baseSpecBuilder();

        basicAuthScheme.setUserName("adminNoNoNo");
        basicAuthScheme.setPassword("adminYesYesYes");

        authBaseSpecBuilder.setAuth(basicAuthScheme);

        return authBaseSpecBuilder.build();
    }
}
