package com.bhft.todo.get;


import com.bhft.todo.BaseTest;
import com.todo.requests.TodoRequest;
import com.todo.requests.ValidatedTodoRequest;
import com.todo.specs.RequestSpec;
import io.qameta.allure.*;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import com.todo.models.Todo;

import java.util.List;

@Epic("TODO Management")
@Feature("Get Todos API")
public class GetTodosTests extends BaseTest {

    @BeforeEach
    public void setupEach() {
        deleteAllTodos();
    }

    @Test
    @Tag("Positive")
    @Description("Get empty ToDo list.")
    @DisplayName("Get empty ToDo list.")
    public void testGetTodosWhenDatabaseIsEmpty() {
        TodoRequest todoRequest = new TodoRequest(RequestSpec.unauthSpec());
        todoRequest.getAll()
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("", hasSize(0));
    }

    @Test
    @Tag("Positive")
    @Description("Get ToDo list with existing records")
    @DisplayName("Get ToDo list with existing records")
    public void testGetTodosWithExistingEntries() {
        Todo todo1 = new Todo(1, "Task 1", false);
        Todo todo2 = new Todo(2, "Task 2", true);

        ValidatedTodoRequest validatedTodoRequest = new ValidatedTodoRequest(RequestSpec.unauthSpec());
        validatedTodoRequest.create(todo1);
        validatedTodoRequest.create(todo2);


        Response response = new TodoRequest(RequestSpec.unauthSpec()).getAll()
                        .then()
                        .statusCode(200)
                        .contentType("application/json")
                        .body("", hasSize(2))
                        .extract().response();

        // Дополнительная проверка содержимого
        Todo[] todos = response.getBody().as(Todo[].class);

        Assertions.assertEquals(1, todos[0].getId());
        Assertions.assertEquals("Task 1", todos[0].getText());
        Assertions.assertFalse(todos[0].isCompleted());

        Assertions.assertEquals(2, todos[1].getId());
        Assertions.assertEquals("Task 2", todos[1].getText());
        Assertions.assertTrue(todos[1].isCompleted());
    }

    @Test
    @Tag("Positive")
    @Description("Check pagination: offset and limit.")
    public void testGetTodosWithOffsetAndLimit() {
        ValidatedTodoRequest validatedTodoRequest = new ValidatedTodoRequest(RequestSpec.unauthSpec());
        for (int i = 1; i <= 5; i++) {
            validatedTodoRequest.create(new Todo(i, "Task " + i, i % 2 == 0));
        }

        // Проверяем, что получили задачи с id 3 и 4
        List<Todo> todos = validatedTodoRequest.getAll(2,2);

        Assertions.assertEquals(3, todos.getFirst().getId());
        Assertions.assertEquals("Task 3", todos.getFirst().getText());

        Assertions.assertEquals(4, todos.get(1).getId());
        Assertions.assertEquals("Task 4", todos.get(1).getText());
    }

    @Test
    @Tag("Negative")
    @DisplayName("Set negative offset")
    public void testSetNegativeOffset() {
        TodoRequest todoRequest = new TodoRequest(RequestSpec.unauthSpec());
        todoRequest.getAll(-1, 2)
                .then()
                .statusCode(400)
                .contentType("text/plain")
                .body(containsString("Invalid query string"));
    }

    @Test
    @Tag("Negative")
    @Description("Set limit only, offset is empty")
    @DisplayName("Set limit only, offset is empty")
    public void testSetOnlyLimit() {
        ValidatedTodoRequest validatedTodoRequest = new ValidatedTodoRequest(RequestSpec.unauthSpec());
        validatedTodoRequest.getAll(null, 2);
    }

    @Test
    @DisplayName("Check max limit value")
    public void testGetTodosWithExcessiveLimit() {
        ValidatedTodoRequest validatedTodoRequest = new ValidatedTodoRequest(RequestSpec.unauthSpec());
        for (int i = 1; i <= 10; i++) {
            validatedTodoRequest.create(new Todo(i, "Task " + i, i % 2 == 0));
        }

        List<Todo> todos = validatedTodoRequest.getAll(null, 1000);

        Assertions.assertEquals(10, todos.size());
    }

    @Test
    @Tag("Contract test")
    @DisplayName("Set invalid data type to limit")
    public void testSetInvalidDataTypeForLimit() {
        given()
                .filter(new AllureRestAssured())
                .queryParam("offset", 0)
                .queryParam("limit", "abc")
                .when()
                .get("/todos")
                .then()
                .statusCode(400)
                .contentType("text/plain")
                .body(containsString("Invalid query string"));
    }
}
