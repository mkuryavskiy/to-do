package ua.edu.chnu.tasks_api.tasks;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class TaskService {
    private final TaskRepository repository;

    public Task create(Task task) {
        return repository.save(task);
    }

    public List<Task> readAll() {
        return repository.findAll();
    }

    public Task read(int id) {
        return repository.findById(id).orElse(null);
    }

    public void update(int id, Task task) {
        Task updated = read(id);
        if (updated == null) {
            return;
        }

        updated.setName(task.getName());
        repository.save(updated);
    }

    public void delete(int id) {
        Task task = read(id);
        if (task == null) {
            return;
        }

        repository.delete(task);
    }
}