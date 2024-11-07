package ua.edu.chnu.task_groups_api.task_groups;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/task-groups")
public class TaskGroupController {
    private final TaskGroupService service;

    @GetMapping
    public ResponseEntity<List<TaskGroup>> readAll() {
        var taskGroups = service.readAll();
        return ResponseEntity.ok(taskGroups);
    }

    @GetMapping("{id}")
    public ResponseEntity<TaskGroup> read(@PathVariable int id) {
        TaskGroup taskGroup = service.read(id);
        return taskGroup == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(taskGroup);
    }

    @PostMapping
    public ResponseEntity<TaskGroup> create(@RequestBody TaskGroup taskGroup) {
        TaskGroup created = service.create(taskGroup);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("{id}")
    public ResponseEntity<Void> update(@PathVariable int id, @RequestBody TaskGroup taskGroup) {
        TaskGroup existed = service.read(id);
        if (existed == null) {
            return ResponseEntity.notFound().build();
        }

        service.update(id, taskGroup);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        TaskGroup taskGroup = service.read(id);
        if (taskGroup == null) {
            return ResponseEntity.notFound().build();
        }

        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}