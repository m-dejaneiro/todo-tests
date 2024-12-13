package com.todo.requests;

import com.todo.models.Todo;
import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;

import java.net.http.HttpRequest;
import java.util.List;

public class ValidatedTodoRequest extends Request implements CrudInterface<Todo>, SearchInterface {

    private TodoRequest todoRequest;

    public ValidatedTodoRequest(RequestSpecification reqSpec) {
        super(reqSpec);
        todoRequest = new TodoRequest(reqSpec);
    }

    @Step("Create ToDo. Status code 201 and empty/null string has been received.")
    @Override
    public String create(Todo entity) {
        return todoRequest.create(entity)
                .then()
                .statusCode(201).extract().asString();
    }

    @Step("Update ToDo. Status code 200 and updated ToDo has been received.")
    @Override
    public ValidatableResponse update(long id, Todo entity) {
        return todoRequest.update(id, entity)
                .then()
                .statusCode(200);
    }

    @Step("Delete ToDo. Status code 200 and empty/null string has been received.")
    @Override
    public String delete(long id) {
        return todoRequest.delete(id)
                .then()
                .statusCode(204).extract().asString();
    }

    @Step("Get ToDos with offset {0}, limit {1}. Status code 200 and list of todos has been received.")
    @Override
    public List<Todo> getAll(Integer offset, Integer limit) {
        Todo[] todos = todoRequest.getAll(offset, limit)
                .then()
                .statusCode(200)
                .extract()
                .as(Todo[].class);

        return List.of(todos);
    }

    @Step("Get All ToDos. Status code 200 and list of todos has been received.")
    @Override
    public List<Todo> getAll() {
        Todo[] todos = todoRequest.getAll()
                .then()
                .statusCode(200)
                .extract()
                .as(Todo[].class);

        return List.of(todos);
    }
}
