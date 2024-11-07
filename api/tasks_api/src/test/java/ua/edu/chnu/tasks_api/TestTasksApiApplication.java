package ua.edu.chnu.tasks_api;

import org.springframework.boot.SpringApplication;

public class TestTasksApiApplication {
    public static void main(String[] args) {
        SpringApplication.from(TasksApiApplication::main).with(TestcontainersConfiguration.class).run(args);
    }
}