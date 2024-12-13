package com.bhft.todo.post;

import com.bhft.todo.BaseTest;
import com.todo.models.Todo;
import com.todo.requests.TodoRequest;
import com.todo.requests.ValidatedTodoRequest;
import com.todo.specs.RequestSpec;
import io.qameta.allure.Description;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.requestSpecification;
import static org.hamcrest.Matchers.*;

public class PostTodosTests extends BaseTest {

    @BeforeEach
    public void setupEach() {
        deleteAllTodos();
    }

    @Test
    @Tag("positive")
    @Description("Create new ToDo with valid data.")
    public void testCreateTodoWithValidData() {
        Todo newTodo = new Todo(1, "New Task", false);

        ValidatedTodoRequest validatedTodoRequest = new ValidatedTodoRequest(RequestSpec.unauthSpec());

        validatedTodoRequest.create(newTodo);
        List<Todo> todos = validatedTodoRequest.getAll();

        boolean found = false;
        for (Todo todo : todos) {
            if (todo.getId() == newTodo.getId()) {
                Assertions.assertEquals(newTodo.getText(), todo.getText());
                Assertions.assertEquals(newTodo.isCompleted(), todo.isCompleted());
                found = true;
                break;
            }
        }
        Assertions.assertTrue(found, "Созданная задача не найдена в списке TODO");
    }

    @Test
    @Tag("Positive")
    @Description("Create ToDo with max length of the text.")
    public void testCreateTodoWithMaxLengthText() {
        String maxLengthText = "A".repeat(255);
        Todo newTodo = new Todo(3, maxLengthText, false);

        ValidatedTodoRequest validatedTodoRequest = new ValidatedTodoRequest(RequestSpec.unauthSpec());

        validatedTodoRequest.create(newTodo);
        List<Todo> todos = validatedTodoRequest.getAll();

        boolean found = false;
        for (Todo todo : todos) {
            if (todo.getId() == newTodo.getId()) {
                Assertions.assertEquals(newTodo.getText(), todo.getText());
                Assertions.assertEquals(newTodo.isCompleted(), todo.isCompleted());
                found = true;
                break;
            }
        }
        Assertions.assertTrue(found, "Созданная задача не найдена в списке TODO");
    }


    @Test
    @Tag("Negative")
    @Description("Create ToDo with existing ID.")
    public void testCreateTodoWithExistingId() {
        Todo firstTodo = new Todo(5, "First Task", false);
        ValidatedTodoRequest validatedTodoRequest = new ValidatedTodoRequest(RequestSpec.unauthSpec());
        validatedTodoRequest.create(firstTodo);

        Todo duplicateTodo = new Todo(5, "Duplicate Task", true);
        TodoRequest duplicateTodoRequest = new TodoRequest(RequestSpec.unauthSpec());
        duplicateTodoRequest.create(duplicateTodo)
                .then()
                .statusCode(400)
                //.contentType(ContentType.TEXT)
                .body(is(notNullValue()));
    }

    @Test
    @Tag("Contract test")
    public void testCreateTodoWithMissingFields() {
        String invalidTodoJson = "{ \"id\": 2, \"completed\": true }";

        given()
                .filter(new AllureRestAssured())
                .contentType(ContentType.JSON)
                .body(invalidTodoJson)
                .when()
                .post("/todos")
                .then()
                .statusCode(400)
                .contentType(ContentType.TEXT)
                .body(notNullValue()); // Проверяем, что есть сообщение об ошибке
    }

    @Test
    @Tag("Negative")
    @Description("Create ToDo with too big length of the text")
    public void testCreateTodoWithTooBigTextLength() {
        String maxLengthText = "A".repeat(100000);
        Todo newTodo = new Todo(3, maxLengthText, false);

        TodoRequest todoRequest = new TodoRequest(RequestSpec.authSpec());


        todoRequest.create(newTodo)
                .then()
                .statusCode(413)
                .contentType(ContentType.TEXT)
                .body(notNullValue()); // Проверяем, что есть сообщение об ошибке
    }

}
