package com.todo.requests;

import com.todo.models.Todo;
import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.List;

import static io.restassured.RestAssured.given;

public class TodoRequest extends Request implements CrudInterface<Todo>, SearchInterface {
    private static final String TODO_ENDPOINT = "/todos";

    public TodoRequest(RequestSpecification reqSpec) {
        super(reqSpec);
    }

    @Step("POST. Create ToDo {0}.")
    @Override
    public Response create(Todo entity) {
        return given()
                .spec(reqSpec)
                .body(entity)
                .when()
                .post(TODO_ENDPOINT);
    }

    @Step("PUT. Update ToDo id {0}. New Todo {1}")
    @Override
    public Response update(long id, Todo entity) {

        return given()
                .spec(reqSpec)
                .body(entity)
                .put(TODO_ENDPOINT + "/" + id);
    }

    @Step("DELETE. Delete ToDo by id {0}.")
    @Override
    public Response delete(long id) {
        return given()
                .spec(reqSpec)
                .delete(TODO_ENDPOINT + "/" + id);
    }

    @Step("GET. Get ToDo with offset {0} and limit {1}.")
    @Override
    public Response getAll(Integer offset, Integer limit) {
        RequestSpecification rs = given();
        if (offset != null) {
            rs.queryParam("offset", offset);
        }
        if (limit != null) {
            rs.queryParam("limit", limit);
        }

        return rs
                .when()
                .get(TODO_ENDPOINT);
    }

    @Step("GET. Get all ToDo.")
    @Override
    public Response getAll() {
        return given()
                .when()
                .get(TODO_ENDPOINT);
    }
}
