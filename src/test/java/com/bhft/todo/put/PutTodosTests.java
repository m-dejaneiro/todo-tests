package com.bhft.todo.put;

import com.bhft.todo.BaseTest;
import com.todo.requests.TodoRequest;
import com.todo.requests.ValidatedTodoRequest;
import com.todo.specs.RequestSpec;
import io.qameta.allure.Description;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import com.todo.models.Todo;

import java.util.List;

public class PutTodosTests extends BaseTest {

    @BeforeEach
    public void setupEach() {
        deleteAllTodos();
    }


    @Test
    @Tag("Positive")
    @Description("Update existing ToDo with valid data")
    public void testUpdateExistingTodoWithValidData() {
        Todo originalTodo = new Todo(1, "Original Task", false);
        ValidatedTodoRequest validatedTodoRequest = new ValidatedTodoRequest(RequestSpec.unauthSpec());
        validatedTodoRequest.create(originalTodo);

        Todo updatedTodo = new Todo(1, "Updated Task", true);
        ValidatedTodoRequest updateExistingTodoAndValidate = new ValidatedTodoRequest(RequestSpec.unauthSpec());
        updateExistingTodoAndValidate.update(originalTodo.getId(), updatedTodo);

        List<Todo> todos = updateExistingTodoAndValidate.getAll();

        Assertions.assertEquals(1, todos.size());
        Assertions.assertEquals("Updated Task", todos.getFirst().getText());
        Assertions.assertTrue(todos.getFirst().isCompleted());
    }


    @Test
    @Tag("Negative")
    @Description("Update not-existing ToDo, not-existing ID.")
    public void testUpdateNonExistentTodo() {
        Todo updatedTodo = new Todo(999, "Non-existent Task", true);

        TodoRequest todoRequest = new TodoRequest(RequestSpec.unauthSpec());
        todoRequest.update(updatedTodo.getId(), updatedTodo)
                .then()
                .statusCode(404)
                //.contentType(ContentType.TEXT)
                .body(is(notNullValue()));
    }

    @Test
    @Tag("negative")
    @Description("Update existing ToDo without changes")
    public void testUpdateTodoWithoutChangingData() {
        Todo originalTodo = new Todo(4, "Task without Changes", false);
        ValidatedTodoRequest validatedTodoRequest = new ValidatedTodoRequest(RequestSpec.unauthSpec());
        validatedTodoRequest.create(originalTodo);

        validatedTodoRequest.update(originalTodo.getId(), originalTodo);

        List<Todo> todos = validatedTodoRequest.getAll();

        Assertions.assertEquals("Task without Changes", todos.getFirst().getText());
        Assertions.assertFalse(todos.getFirst().isCompleted());
    }

    @Test
    @Tag("Contract test")
    public void testUpdateTodoWithMissingFields() {
        Todo originalTodo = new Todo(2, "Task to Update", false);
        createTodo(originalTodo);

        // Обновленные данные с отсутствующим полем 'text'
        String invalidTodoJson = "{ \"id\": 2, \"completed\": true }";

        given()
                .filter(new AllureRestAssured())
                .contentType(ContentType.JSON)
                .body(invalidTodoJson)
                .when()
                .put("/todos/2")
                .then()
                .statusCode(401);
        //.contentType(ContentType.JSON)
        //.body("error", containsString("Missing required field 'text'"));
    }

    @Test
    @Tag("Contract test")
    public void testUpdateTodoWithInvalidDataTypes() {
        Todo originalTodo = new Todo(3, "Another Task", false);
        createTodo(originalTodo);

        // Обновленные данные с некорректным типом поля 'completed'
        String invalidTodoJson = "{ \"id\": 3, \"text\": \"Updated Task\", \"completed\": \"notBoolean\" }";

        given()
                .filter(new AllureRestAssured())
                .contentType(ContentType.JSON)
                .body(invalidTodoJson)
                .when()
                .put("/todos/3")
                .then()
                .statusCode(401);
    }
}
