package ua.edu.chnu.task_groups_api;

import io.restassured.RestAssured;
import jakarta.annotation.PostConstruct;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import ua.edu.chnu.task_groups_api.task_groups.TaskGroup;

@Import(TestcontainersConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TaskGroupControllerIntegrationTest {
    private static final int NOT_FOUND_TASK_GROUP_ID = Integer.MAX_VALUE;

    private static final TaskGroup READ_TASK_GROUP = new TaskGroup(0, "read"),
            SHOULD_UPDATED_TASK_GROUP = new TaskGroup(0, "should-updated"),
            DELETED_TASK_GROUP = new TaskGroup(0, "deleted"),
            CREATED_TASK_GROUP = new TaskGroup(0, "created"),
            UPDATED_TASK_GROUP = new TaskGroup(0, "updated");

    private static boolean testDataCreated;
    private static int readTaskGroupId, shouldUpdatedTaskGroupId, deletedTaskGroupId;

    @LocalServerPort
    private int port;

    private String url;

    @PostConstruct
    void init() {
        url = "http://localhost:" + port + "/api/task-groups";

        if (testDataCreated) {
            return;
        }

        readTaskGroupId = RestAssured.given()
                .contentType("application/json")
                .body(READ_TASK_GROUP)
                .post(url)
                .then()
                .extract()
                .jsonPath()
                .get("id");

        shouldUpdatedTaskGroupId = RestAssured.given()
                .contentType("application/json")
                .body(SHOULD_UPDATED_TASK_GROUP)
                .post(url)
                .then()
                .extract()
                .jsonPath()
                .get("id");

        deletedTaskGroupId = RestAssured.given()
                .contentType("application/json")
                .body(DELETED_TASK_GROUP)
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
                .body("[0].id", Matchers.equalTo(readTaskGroupId))
                .body("[0].name", Matchers.equalTo(READ_TASK_GROUP.getName()))
                .statusCode(HttpStatus.OK.value());
    }

    @Test
    void testCreate() {
        int id = RestAssured.given()
                .contentType("application/json")
                .body(CREATED_TASK_GROUP)
                .post(url)
                .then()
                .body(Matchers.notNullValue())
                .body("name", Matchers.equalTo(CREATED_TASK_GROUP.getName()))
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .jsonPath()
                .get("id");

        RestAssured.get(url + "/" + id)
                .then()
                .body(Matchers.notNullValue())
                .body("id", Matchers.equalTo(id))
                .body("name", Matchers.equalTo(CREATED_TASK_GROUP.getName()))
                .statusCode(HttpStatus.OK.value());
    }

    @Test
    void testUpdate() {
        RestAssured.given()
                .contentType("application/json")
                .body(UPDATED_TASK_GROUP)
                .put(url + "/" + shouldUpdatedTaskGroupId)
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());

        RestAssured.get(url + "/" + shouldUpdatedTaskGroupId)
                .then()
                .body(Matchers.notNullValue())
                .body("id", Matchers.equalTo(shouldUpdatedTaskGroupId))
                .body("name", Matchers.equalTo(UPDATED_TASK_GROUP.getName()));
    }

    @Test
    void testUpdateNotFound() {
        RestAssured.given()
                .contentType("application/json")
                .body(UPDATED_TASK_GROUP)
                .put(url + "/" + NOT_FOUND_TASK_GROUP_ID)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void testDelete() {
        RestAssured.delete(url + "/" + deletedTaskGroupId)
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());

        RestAssured.get(url + "/" + deletedTaskGroupId)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void testDeleteNotFound() {
        RestAssured.delete(url + "/" + NOT_FOUND_TASK_GROUP_ID)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }
}