package ua.edu.chnu.tasks_api;

import io.restassured.RestAssured;
import jakarta.annotation.PostConstruct;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import ua.edu.chnu.tasks_api.tasks.Task;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(TestcontainersConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TaskControllerIntegrationTest {
    private static final int NOT_FOUND_TASK_ID = Integer.MAX_VALUE;

    private static final Task READ_TASK = new Task(0, "read"),
            SHOULD_UPDATED_TASK = new Task(0, "should-updated"),
            DELETED_TASK = new Task(0, "deleted"),
            CREATED_TASK = new Task(0, "created"),
            UPDATED_TASK = new Task(0, "updated");

    private static boolean testDataCreated;
    private static int readTaskId, shouldUpdatedTaskId, deletedTaskId;

    @LocalServerPort
    private int port;

    private String url;

    @PostConstruct
    void init() {
        url = "http://localhost:" + port + "/api/tasks";

        if (testDataCreated) {
            return;
        }

        readTaskId = RestAssured.given()
                .contentType("application/json")
                .body(READ_TASK)
                .post(url)
                .then()
                .extract()
                .jsonPath()
                .get("id");

        shouldUpdatedTaskId = RestAssured.given()
                .contentType("application/json")
                .body(SHOULD_UPDATED_TASK)
                .post(url)
                .then()
                .extract()
                .jsonPath()
                .get("id");

        deletedTaskId = RestAssured.given()
                .contentType("application/json")
                .body(DELETED_TASK)
                .post(url)
                .then()
                .extract()
                .jsonPath()
                .get("id");

        testDataCreated = true;
    }

    @Test
    void testReadAll() {
        RestAssured.get(url)
                .then()
                .body(Matchers.notNullValue())
                .body("[0]", Matchers.notNullValue())
                .body("[0].id", Matchers.equalTo(readTaskId))
                .body("[0].name", Matchers.equalTo(READ_TASK.getName()))
                .statusCode(HttpStatus.OK.value());
    }

    @Test
    void testCreate() {
        int id = RestAssured.given()
                .contentType("application/json")
                .body(CREATED_TASK)
                .post(url)
                .then()
                .body(Matchers.notNullValue())
                .body("name", Matchers.equalTo(CREATED_TASK.getName()))
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .jsonPath()
                .get("id");

        RestAssured.get(url + "/" + id)
                .then()
                .body(Matchers.notNullValue())
                .body("id", Matchers.equalTo(id))
                .body("name", Matchers.equalTo(CREATED_TASK.getName()))
                .statusCode(HttpStatus.OK.value());
    }

    @Test
    void testUpdate() {
        RestAssured.given()
                .contentType("application/json")
                .body(UPDATED_TASK)
                .put(url + "/" + shouldUpdatedTaskId)
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());

        RestAssured.get(url + "/" + shouldUpdatedTaskId)
                .then()
                .body(Matchers.notNullValue())
                .body("id", Matchers.equalTo(shouldUpdatedTaskId))
                .body("name", Matchers.equalTo(UPDATED_TASK.getName()));
    }

    @Test
    void testUpdateNotFound() {
        RestAssured.given()
                .contentType("application/json")
                .body(UPDATED_TASK)
                .put(url + "/" + NOT_FOUND_TASK_ID)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void testDelete() {
        RestAssured.delete(url + "/" + deletedTaskId)
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());

        RestAssured.get(url + "/" + deletedTaskId)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void testDeleteNotFound() {
        RestAssured.delete(url + "/" + NOT_FOUND_TASK_ID)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }
}