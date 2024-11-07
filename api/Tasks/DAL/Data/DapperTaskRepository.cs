using DAL.Models;
using Dapper;
using Npgsql;

namespace DAL.Data;

public class DapperTaskRepository(NpgsqlDataSource dataSource) : ITaskRepository
{
    public async Task<int> CreateAsync(TaskModel task)
    {
        await using NpgsqlConnection connection = await dataSource.OpenConnectionAsync();
        const string sql = "INSERT INTO tasks (name) VALUES (@Name) RETURNING id";
        var parameters = new { task.Name };
        int id = await connection.ExecuteScalarAsync<int>(sql, parameters);
        return id;
    }

    public async Task<IEnumerable<TaskModel>> ReadAllAsync()
    {
        await using NpgsqlConnection connection = await dataSource.OpenConnectionAsync();
        const string sql = "SELECT * FROM tasks";
        var tasks = await connection.QueryAsync<TaskModel>(sql);
        return tasks;
    }

    public async Task<TaskModel?> ReadAsync(int id)
    {
        await using NpgsqlConnection connection = await dataSource.OpenConnectionAsync();
        const string sql = "SELECT * FROM tasks WHERE id = @Id";
        var parameter = new { Id = id };
        TaskModel? task = await connection.QuerySingleOrDefaultAsync<TaskModel>(sql, parameter);
        return task;
    }

    public async Task UpdateAsync(int id, TaskModel task)
    {
        await using NpgsqlConnection connection = await dataSource.OpenConnectionAsync();
        const string sql = "UPDATE tasks SET name = @Name WHERE id = @Id";
        var parameters = new { task.Name, Id = id };
        await connection.ExecuteAsync(sql, parameters);
    }

    public async Task DeleteAsync(int id)
    {
        await using NpgsqlConnection connection = await dataSource.OpenConnectionAsync();
        const string sql = "DELETE FROM tasks WHERE id = @Id";
        var parameter = new { Id = id };
        await connection.ExecuteAsync(sql, parameter);
    }
}