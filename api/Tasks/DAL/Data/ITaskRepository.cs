using DAL.Models;

namespace DAL.Data;

public interface ITaskRepository
{
    Task<int> CreateAsync(TaskModel task);
    Task<IEnumerable<TaskModel>> ReadAllAsync();
    Task<TaskModel?> ReadAsync(int id);
    Task UpdateAsync(int id, TaskModel task);
    Task DeleteAsync(int id);
}