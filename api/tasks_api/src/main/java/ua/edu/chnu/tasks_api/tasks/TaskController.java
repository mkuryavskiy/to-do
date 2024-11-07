package ua.edu.chnu.tasks_api.tasks;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/tasks")
public class TaskController {
    private final TaskService service;

    @GetMapping
    public ResponseEntity<List<Task>> readAll() {
        var tasks = service.readAll();
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("{id}")
    public ResponseEntity<Task> read(@PathVariable int id) {
        Task task = service.read(id);
        return task == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(task);
    }

    @PostMapping
    public ResponseEntity<Task> create(@RequestBody Task task) {
        Task created = service.create(task);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("{id}")
    public ResponseEntity<Void> update(@PathVariable int id, @RequestBody Task task) {
        Task existed = service.read(id);
        if (existed == null) {
            return ResponseEntity.notFound().build();
        }

        service.update(id, task);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        Task task = service.read(id);
        if (task == null) {
            return ResponseEntity.notFound().build();
        }
        
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}