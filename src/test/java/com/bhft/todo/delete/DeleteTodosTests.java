package com.bhft.todo.delete;

import com.bhft.todo.BaseTest;

import com.bhft.todo.BaseTest;
import com.todo.requests.TodoRequest;
import com.todo.requests.ValidatedTodoRequest;
import com.todo.specs.RequestSpec;
import io.qameta.allure.Description;
import io.qameta.allure.restassured.AllureRestAssured;
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

public class DeleteTodosTests extends BaseTest {

    @BeforeEach
    public void setupEach() {
        deleteAllTodos();
    }

    @Test
    @Tag("Positive")
    @Description("Admin. Delete existing ToDo.")
    public void testDeleteExistingTodoWithValidAuth() {
        Todo todo = new Todo(1, "Task to Delete", false);
        ValidatedTodoRequest validatedTodoRequest = new ValidatedTodoRequest(RequestSpec.authSpec());
        validatedTodoRequest.create(todo);

        validatedTodoRequest.delete(todo.getId());

        List<Todo> todos = validatedTodoRequest.getAll();

        boolean found = false;
        for (Todo t : todos) {
            if (t.getId() == todo.getId()) {
                found = true;
                break;
            }
        }
        Assertions.assertFalse(found, "Удаленная задача все еще присутствует в списке TODO");
    }

    @Test
    @Tag("Negative")
    @Description("Not-Authorized user. Delete ToDo.")
    public void testDeleteTodoWithoutAuthHeader() {
        ValidatedTodoRequest validatedTodoRequest = new ValidatedTodoRequest(RequestSpec.unauthSpec());
        Todo todo = new Todo(2, "Task to Delete", false);

        validatedTodoRequest.create(todo);

        TodoRequest todoRequest = new TodoRequest(RequestSpec.unauthSpec());
        todoRequest.delete(todo.getId())
                .then()
                .statusCode(401);

        List<Todo> todos = validatedTodoRequest.getAll();

        boolean found = false;
        for (Todo t : todos) {
            if (t.getId() == todo.getId()) {
                found = true;
                break;
            }
        }
        Assertions.assertTrue(found, "Задача отсутствует в списке TODO, хотя не должна была быть удалена");
    }

    @Test
    public void testDeleteTodoWithInvalidAuth() {
        TodoRequest todoRequest = new TodoRequest(RequestSpec.invalidCredsSpec());
        ValidatedTodoRequest validatedTodoRequest = new ValidatedTodoRequest(RequestSpec.unauthSpec());
        Todo todo = new Todo(3, "Task to Delete", false);

        validatedTodoRequest.create(todo);

        todoRequest.delete(todo.getId())
                .then()
                .statusCode(401);

        List<Todo> todos = validatedTodoRequest.getAll();

        boolean found = false;
        for (Todo t : todos) {
            if (t.getId() == todo.getId()) {
                found = true;
                break;
            }
        }
        Assertions.assertTrue(found, "Задача отсутствует в списке TODO, хотя не должна была быть удалена");
    }

    @Test
    @Tag("Negative")
    @Description("Delete nonexisting ToDo.")
    public void testDeleteNonExistentTodo() {
        long id = 567;
        TodoRequest todoRequest = new TodoRequest(RequestSpec.authSpec());

        todoRequest.delete(id)
                .then()
                .statusCode(404);
    }

    @Test
    @Tag("Contract test")
    @Description("Delete ToDo with invalid data type of ID.")
    public void testDeleteTodoWithInvalidIdFormat() {
        // Отправляем DELETE запрос с некорректным id
        given()
                .filter(new AllureRestAssured())
                .auth()
                .preemptive()
                .basic("admin", "admin")
                .when()
                .delete("/todos/invalidId")
                .then()
                .statusCode(404);
    }
}
