package ua.edu.chnu.task_groups_api;

import org.springframework.boot.SpringApplication;

public class TestTaskGroupsApplication {
    public static void main(String[] args) {
        SpringApplication.from(TaskGroupsApplication::main).with(TestcontainersConfiguration.class).run(args);
    }
}