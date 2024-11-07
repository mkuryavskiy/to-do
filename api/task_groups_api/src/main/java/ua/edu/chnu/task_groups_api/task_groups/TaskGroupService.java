package ua.edu.chnu.task_groups_api.task_groups;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class TaskGroupService {
    private final TaskGroupRepository repository;

    public TaskGroup create(TaskGroup taskGroup) {
        return repository.save(taskGroup);
    }

    public List<TaskGroup> readAll() {
        return repository.findAll();
    }

    public TaskGroup read(int id) {
        return repository.findById(id).orElse(null);
    }

    public void update(int id, TaskGroup taskGroup) {
        TaskGroup updated = read(id);
        if (updated == null) {
            return;
        }

        updated.setName(taskGroup.getName());
        repository.save(updated);
    }

    public void delete(int id) {
        TaskGroup taskGroup = read(id);
        if (taskGroup == null) {
            return;
        }

        repository.delete(taskGroup);
    }
}